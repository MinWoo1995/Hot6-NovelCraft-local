package com.example.hot6novelcraft.domain.file.service;

import com.example.hot6novelcraft.common.exception.ServiceErrorException;
import com.example.hot6novelcraft.common.exception.domain.FileExceptionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "pdf", "txt", "docx", "xlsx");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif",
            "application/pdf", "text/plain",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.region.static}")
    private String region;

    /**
     * 채팅 첨부 파일을 S3에 업로드하고 URL 반환
     */
    public String uploadChatFile(MultipartFile file) {
        validateFile(file);

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String s3Key = generateS3Key(extension);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

            String fileUrl = buildS3Url(s3Key);
            log.info("[S3] 파일 업로드 성공: {} -> {}", truncateFilename(originalFilename), fileUrl);
            return fileUrl;

        } catch (IOException e) {
            log.error("[S3] 파일 읽기 실패: {}", truncateFilename(originalFilename), e);
            throw new ServiceErrorException(FileExceptionEnum.ERR_FILE_UPLOAD_FAILED);
        } catch (Exception e) {
            log.error("[S3] S3 업로드 실패: {}", truncateFilename(originalFilename), e);
            throw new ServiceErrorException(FileExceptionEnum.ERR_FILE_UPLOAD_FAILED);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ServiceErrorException(FileExceptionEnum.ERR_FILE_EMPTY);
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ServiceErrorException(FileExceptionEnum.ERR_FILE_TOO_LARGE);
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new ServiceErrorException(FileExceptionEnum.ERR_FILE_EMPTY);
        }

        String extension = extractExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new ServiceErrorException(FileExceptionEnum.ERR_FILE_NOT_SUPPORTED);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new ServiceErrorException(FileExceptionEnum.ERR_FILE_NOT_SUPPORTED);
        }
    }

    private String extractExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            throw new ServiceErrorException(FileExceptionEnum.ERR_FILE_NOT_SUPPORTED);
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    private String generateS3Key(String extension) {
        return "chat/" + UUID.randomUUID() + "." + extension;
    }

    private String buildS3Url(String s3Key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, s3Key);
    }

    private String truncateFilename(String filename) {
        final int MAX_LENGTH = 50;
        if (filename == null || filename.length() <= MAX_LENGTH) {
            return filename;
        }
        return filename.substring(0, MAX_LENGTH) + "...";
    }
}

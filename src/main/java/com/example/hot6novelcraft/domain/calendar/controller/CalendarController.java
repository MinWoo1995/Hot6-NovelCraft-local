package com.example.hot6novelcraft.domain.calendar.controller;

import com.example.hot6novelcraft.common.dto.BaseResponse;
import com.example.hot6novelcraft.common.dto.PageResponse;
import com.example.hot6novelcraft.domain.calendar.dto.*;
import com.example.hot6novelcraft.domain.calendar.service.CalendarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    // 임시 userId - 추후 Spring Security로 교체 예정
    //Security 붙이면 TEMP_USER_ID → @AuthenticationPrincipal로 교체
    private static final Long TEMP_USER_ID = 1L;

    @PostMapping("/me/records")
    public ResponseEntity<BaseResponse<ReadingRecordCreateResponse>> createReadingRecord(
            @Valid @RequestBody ReadingRecordCreateRequest request
    ) {
        ReadingRecordCreateResponse response = calendarService.createReadingRecord(TEMP_USER_ID, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.success(HttpStatus.CREATED.name(), "독서 기록이 성공적으로 등록되었습니다", response));
    }
    @GetMapping("/me/records")
    public ResponseEntity<BaseResponse<PageResponse<ReadingRecordResponse>>> getReadingRecords(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) Long novelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse<ReadingRecordResponse> response = calendarService.getReadingRecords(
                TEMP_USER_ID, date, novelId, PageRequest.of(page, size)
        );
        return ResponseEntity.ok(BaseResponse.success(HttpStatus.OK.name(), "독서 기록 조회 성공", response));
    }
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<List<CalendarDailyResponse>>> getCalendarRecords(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<CalendarDailyResponse> response = calendarService.getCalendarRecords(
                TEMP_USER_ID, startDate, endDate
        );
        return ResponseEntity.ok(
                BaseResponse.success(HttpStatus.OK.name(), "독서 캘린더 데이터 조회가 완료되었습니다", response)
        );
    }
    @GetMapping("/me/statistics")
    public ResponseEntity<BaseResponse<MonthlyStatResponse>> getMonthlyStatistics(
            @RequestParam int year,
            @RequestParam int month
    ) {
        MonthlyStatResponse response = calendarService.getMonthlyStatistics(
                TEMP_USER_ID, year, month
        );
        return ResponseEntity.ok(
                BaseResponse.success(HttpStatus.OK.name(), "월간 통계 조회 성공", response)
        );
    }
}

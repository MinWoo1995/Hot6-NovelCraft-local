package com.example.hot6novelcraft.domain.user.controller;

import com.example.hot6novelcraft.common.dto.BaseResponse;
import com.example.hot6novelcraft.domain.user.dto.response.AuthorFollowResponse;
import com.example.hot6novelcraft.domain.user.entity.UserDetailsImpl;
import com.example.hot6novelcraft.domain.user.service.AuthorFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorFollowController {

    private final AuthorFollowService authorFollowService;

    /**
     * 작가 팔로우/취소
     * 정은식
     */
    @PostMapping("/{authorId}/follow")
    public ResponseEntity<BaseResponse<AuthorFollowResponse>> toggleFollow(
            @PathVariable Long authorId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        AuthorFollowResponse response = authorFollowService.toggleFollow(authorId, userDetails);

        String message = response.isFollowing() ? "팔로우 성공" : "팔로우 취소";

        return ResponseEntity.ok(
                BaseResponse.success("OK", message, response)
        );
    }
}
package com.example.hot6novelcraft.domain.search.controller;

import com.example.hot6novelcraft.common.dto.BaseResponse;
import com.example.hot6novelcraft.common.dto.PageResponse;
import com.example.hot6novelcraft.domain.search.dto.IntegratedAuthorSearchResponse;
import com.example.hot6novelcraft.domain.search.dto.NovelSearchResponse;
import com.example.hot6novelcraft.domain.search.dto.TagGroupSearchResponse;
import com.example.hot6novelcraft.domain.search.service.SearchService;
import com.example.hot6novelcraft.domain.user.entity.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {

    private final SearchService searchService;

    /** ===============================
     1. 제목(소설) 검색
        - GET /api/search/novels?keyword=바다
     2. 태그 검색
        - 복수 검색시 : GET /api/search/tags?tags=FANTASY&tags=MUNCHKIN
     3. 작가 검색
        - GET /api/search/authors?keyword=백산
        - 유사 닉네임 작가 목록 + 유사 키워드 제목 포함 소설 목록
     =================================== */
    @GetMapping("/novels")
    public ResponseEntity<BaseResponse<PageResponse<NovelSearchResponse>>> searchNovels(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Page<NovelSearchResponse> result = searchService.searchNovels(keyword, pageable, userDetails);
        return ResponseEntity.ok(BaseResponse.success("200", "소설 제목 검색 성공", PageResponse.register(result)));
    }

    @GetMapping("/tags")
    public ResponseEntity<BaseResponse<List<TagGroupSearchResponse>>> searchTags(
            @RequestParam List<String> tags,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        List<TagGroupSearchResponse> result = searchService.searchByTags(tags, userDetails);
        return ResponseEntity.ok(BaseResponse.success("200", "소설 태그 검색 성공", result));
    }

    @GetMapping("/authors")
    public ResponseEntity<BaseResponse<IntegratedAuthorSearchResponse>> searchAuthors(
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        IntegratedAuthorSearchResponse result = searchService.searchAuthors(keyword, userDetails);
        return ResponseEntity.ok(BaseResponse.success("200", "작가 검색 성공", result));
    }
}

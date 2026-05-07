package com.example.hot6novelcraft.domain.coverai.dto.request;

public record CoverCreateRequest(
        String title,
        String genre,
        String description
) {}
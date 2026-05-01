package com.example.hot6novelcraft.domain.admin.dto.response;

public record AdminDashboardMentorsStatusResponse(
        long totalMentor
        , long newMentorsToday
) {
    public static AdminDashboardMentorsStatusResponse of(long totalMentor, long newMentorsToday) {
        return new AdminDashboardMentorsStatusResponse(
                totalMentor
                , newMentorsToday
        );
    }
}

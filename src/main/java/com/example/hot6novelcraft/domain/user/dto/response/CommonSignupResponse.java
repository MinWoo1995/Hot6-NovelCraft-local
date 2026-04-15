package com.example.hot6novelcraft.domain.user.dto.response;

public record CommonSignupResponse (

        String TempToken
) {
    public static CommonSignupResponse of(String tempToken) {
        return new CommonSignupResponse(tempToken);
    }
}

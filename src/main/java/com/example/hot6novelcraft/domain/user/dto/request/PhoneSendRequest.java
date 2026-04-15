package com.example.hot6novelcraft.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PhoneSendRequest (

    @NotBlank
    String phoneNo
) {

}

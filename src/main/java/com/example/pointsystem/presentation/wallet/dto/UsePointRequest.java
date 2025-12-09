package com.example.pointsystem.presentation.wallet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record UsePointRequest(
        @Min(1)
        int amount,

        @NotBlank
        String orderNo
) {
}

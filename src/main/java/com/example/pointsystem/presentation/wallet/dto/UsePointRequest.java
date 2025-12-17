package com.example.pointsystem.presentation.wallet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 포인트 사용 요청 본문을 나타내는 DTO입니다.
 */
public record UsePointRequest(
        @Min(1)
        int amount,

        @NotBlank
        String orderNo
) {
}

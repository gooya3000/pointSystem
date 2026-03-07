package com.example.pointsystem.presentation.wallet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * 포인트 사용 요청 본문을 나타내는 DTO입니다.
 */
public record UsePointRequest(
        @Min(value = 1, message = "사용 금액은 1 이상이어야 합니다.")
        int amount,

        @NotBlank(message = "주문번호는 필수입니다.")
        String orderNo
) {
}

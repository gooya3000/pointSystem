package com.example.pointsystem.presentation.wallet.dto;

import jakarta.validation.constraints.Min;

/**
 * 포인트 사용 취소 요청을 표현하는 DTO입니다.
 */
public record CancelUseRequest(
        @Min(value = 1, message = "취소 금액은 1 이상이어야 합니다.")
        int cancelAmount
) {
}

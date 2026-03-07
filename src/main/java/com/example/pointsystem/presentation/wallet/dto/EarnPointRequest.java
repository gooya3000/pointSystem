package com.example.pointsystem.presentation.wallet.dto;

import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 포인트 적립 요청 본문을 표현합니다.
 */
public record EarnPointRequest(
        @Min(value = 1, message = "적립 금액은 1 이상이어야 합니다.")
        int amount,

        // null 이면 기본 만료일(365일) 처리 → EarnedPoint.create() 에서 처리
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime expireAt,

        // 없으면 기본값 MANUAL
        String sourceType
) {
}

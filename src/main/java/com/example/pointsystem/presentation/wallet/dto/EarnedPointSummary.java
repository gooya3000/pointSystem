package com.example.pointsystem.presentation.wallet.dto;

import com.example.pointsystem.domain.wallet.EarnedPoint;

import java.time.LocalDateTime;

/**
 * 적립 포인트 내역을 조회용으로 요약한 응답 모델입니다.
 */
public record EarnedPointSummary(
        Long earnedPointId,
        int amount,
        int remainingAmount,
        LocalDateTime expireAt,
        String sourceType,
        String status,
        LocalDateTime createdAt
) {
    public static EarnedPointSummary from(EarnedPoint ep) {
        return new EarnedPointSummary(
                ep.getEarnedPointId(),
                ep.getAmount(),
                ep.getRemainingAmount(),
                ep.getExpireAt(),
                ep.getSourceType().name(),
                ep.getStatus().name(),
                ep.getCreatedAt()
        );
    }
}

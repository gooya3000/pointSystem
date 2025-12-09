package com.example.pointsystem.presentation.wallet.dto;

import com.example.pointsystem.domain.wallet.PointUsageDetail;

public record PointUsageDetailResponse(
        Long earnedPointId,
        int amount
) {

    public static PointUsageDetailResponse from(PointUsageDetail detail) {
        return new PointUsageDetailResponse(
                detail.getEarnedPointId(),
                detail.getAmount()
        );
    }

}

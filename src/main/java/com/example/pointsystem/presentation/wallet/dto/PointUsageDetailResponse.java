package com.example.pointsystem.presentation.wallet.dto;

import com.example.pointsystem.domain.wallet.PointUsageDetail;

/**
 * 포인트 사용 상세 내역을 응답으로 전달하기 위한 DTO입니다.
 */
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

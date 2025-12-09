package com.example.pointsystem.presentation.wallet.dto;

import com.example.pointsystem.domain.wallet.PointUsage;

import java.time.LocalDateTime;
import java.util.List;

public record PointUsageResponse(
        Long usageId,
        Long memberId,
        String orderNo,
        int amount,
        LocalDateTime createdAt,
        List<PointUsageDetailResponse> details
) {

    public static PointUsageResponse from(PointUsage usage) {
        List<PointUsageDetailResponse> detailResponses = usage.getDetails().stream()
                .map(PointUsageDetailResponse::from)
                .toList();

        return new PointUsageResponse(
                usage.getUsageId(),
                usage.getMemberId(),
                usage.getOrderNo(),
                usage.getUsedAmount(),
                usage.getCreatedAt(),
                detailResponses
        );
    }

}

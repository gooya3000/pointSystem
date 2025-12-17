package com.example.pointsystem.presentation.wallet.dto;

import java.util.List;

/**
 * 포인트 지갑 정보와 적립 내역을 포함한 조회 응답입니다.
 */
public record PointWalletResponse(
        Long memberId,
        int balance,
        List<EarnedPointSummary> points
) {
}

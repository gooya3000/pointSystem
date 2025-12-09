package com.example.pointsystem.presentation.wallet.dto;

import java.util.List;

public record PointWalletResponse(
        Long memberId,
        int balance,
        List<EarnedPointSummary> points
) {
}

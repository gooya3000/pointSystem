package com.example.pointsystem.domain.wallet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class PointWallet {

    private final Long memberId;
    private final List<EarnedPoint> earnedPoints;

}

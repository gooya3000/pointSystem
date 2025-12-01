package com.example.pointsystem.domain.wallet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PointUsageDetail {

    private final Long earnedPointId;
    private final int amount;

}

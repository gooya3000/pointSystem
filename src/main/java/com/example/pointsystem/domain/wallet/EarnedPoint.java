package com.example.pointsystem.domain.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class EarnedPoint {

    private final Long earnedPointId;
    private final int amount;
    private int remainingAmount;
    private final LocalDateTime expireAt;
    private final EarnedPointSourceType sourceType;
    private EarnedPointStatus status;
    private final LocalDateTime createdAt;

}

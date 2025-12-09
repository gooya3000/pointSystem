package com.example.pointsystem.domain.policy;

import lombok.Value;

@Value
public class PointPolicy {
    int maxEarnPerTxn; // 1회 적립 최대 포인트
    int maxBalance;    // 최대 보유 포인트
}

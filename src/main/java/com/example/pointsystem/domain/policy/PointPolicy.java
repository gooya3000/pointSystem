package com.example.pointsystem.domain.policy;

import lombok.Value;

/**
 * 포인트 적립 한도 및 최대 보유 한도를 정의하는 정책 모델입니다.
 */
@Value
public class PointPolicy {
    int maxEarnPerTxn; // 1회 적립 최대 포인트
    int maxBalance;    // 최대 보유 포인트
}

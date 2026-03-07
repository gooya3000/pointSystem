package com.example.pointsystem.domain.wallet;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 포인트 사용과 관련된 도메인 이벤트 정보를 표현합니다.
 */
@RequiredArgsConstructor
public class PointUsageEvent {

    private final PointUsageEventType eventType;  // USE, USE_CANCEL ...
    private final int amount;
    private final List<PointUsageDetail> details;
    private final LocalDateTime createdAt;

    public static PointUsageEvent useCreated(int amount, List<PointUsageDetail> details) {
        return new PointUsageEvent(PointUsageEventType.USE, amount, details, LocalDateTime.now());
    }

    public static PointUsageEvent useCanceled(int amount, List<PointUsageDetail> details) {
        return new PointUsageEvent(PointUsageEventType.USE_CANCEL, amount, details, LocalDateTime.now());
    }

    public static PointUsageEvent usePartiallyCanceled(int amount, List<PointUsageDetail> details) {
        return new PointUsageEvent(PointUsageEventType.USE_PARTIAL, amount, details, LocalDateTime.now());
    }
}

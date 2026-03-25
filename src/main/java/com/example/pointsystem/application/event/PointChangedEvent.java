package com.example.pointsystem.application.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 포인트 상태 변경 사실을 외부로 전달하기 위한 이벤트 모델입니다.
 */
public record PointChangedEvent(
        String eventId,
        PointEventType eventType,
        Long memberId,
        Long usageId,
        Long earnedPointId,
        Integer amount,
        String orderNo,
        LocalDateTime occurredAt
) {
    public static PointChangedEvent of(
            PointEventType eventType,
            Long memberId,
            Long usageId,
            Long earnedPointId,
            Integer amount,
            String orderNo
    ) {
        return new PointChangedEvent(
                UUID.randomUUID().toString(),
                eventType,
                memberId,
                usageId,
                earnedPointId,
                amount,
                orderNo,
                LocalDateTime.now()
        );
    }
}

package com.example.pointsystem.application.event;

/**
 * 포인트 상태 변경 이벤트 유형입니다.
 */
public enum PointEventType {
    EARN,
    USE,
    EARN_CANCEL,
    USE_CANCEL_PARTIAL,
    USE_CANCEL_ALL
}

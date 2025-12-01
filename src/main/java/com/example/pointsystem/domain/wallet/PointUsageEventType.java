package com.example.pointsystem.domain.wallet;


/**
 * 포인트 사용 과정에서 발생하는 이벤트의 유형입니다.
 *
 * USE           : 포인트 사용
 * USE_CANCEL    : 전체 사용 취소
 * USE_PARTIAL   : 부분 사용 취소
 */
public enum PointUsageEventType {
    USE,
    USE_CANCEL,
    USE_PARTIAL
}

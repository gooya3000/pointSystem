package com.example.pointsystem.domain.wallet;

/**
 * 적립 포인트의 상태를 나타냅니다.
 *
 * ACTIVE    : 정상, 사용 가능
 * USED      : 사용완료, 전액 사용되어 잔여 금액 없음
 * CANCELED  : 적립취소, 적립 취소됨
 * EXPIRED   : 만료, 만료되어 사용할 수 없음
 */
public enum EarnedPointStatus {
    ACTIVE,
    USED,
    CANCELED,
    EXPIRED
}

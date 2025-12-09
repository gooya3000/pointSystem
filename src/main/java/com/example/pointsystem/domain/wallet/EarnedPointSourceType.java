package com.example.pointsystem.domain.wallet;

/**
 * 적립 포인트의 발생 유형을 나타냅니다.
 *
 * NORMAL  : 일반 적립 (예: 결제 시 자동 적립)
 * ADMIN   : 운영자/관리자가 수기로 지급한 적립
 * EVENT   : 이벤트/프로모션 적립
 * COMPENSATION : 보상 적립 (환불/장애 보상 등)
 */
public enum EarnedPointSourceType {
    NORMAL,
    ADMIN,
    EVENT,
    COMPENSATION;

    public static EarnedPointSourceType fromNullable(String value) {
        if (value == null || value.isBlank()) {
            return NORMAL; // 기본값
        }
        try {
            return EarnedPointSourceType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            // 잘못된 값이면 예외 던져서 400으로 처리하게
            throw new IllegalArgumentException("Invalid sourceType: " + value);
        }
    }
}

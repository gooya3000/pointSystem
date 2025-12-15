package com.example.pointsystem.domain.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 적립 포인트를 생성합니다. (포인트 적립)
     * @param earnedPointId 적립 포인트 ID
     * @param amount 금액
     * @param remainingAmount 남은 금액
     * @param expireAt 만료일시
     * @param sourceType 적립 포인트 발생 유형
     * @param status 적립 포인트 상태
     * @param createdAt 생성일시
     * @return EarnedPoint
     */
    public static EarnedPoint createPoint(Long earnedPointId, int amount, int remainingAmount, LocalDateTime expireAt, EarnedPointSourceType sourceType, EarnedPointStatus status, LocalDateTime createdAt) {

        if (expireAt == null) {
            expireAt =  LocalDateTime.now().plusDays(365); // 기본 만료일은 365일
        }

        return new EarnedPoint(earnedPointId, amount, remainingAmount, expireAt, sourceType, status, createdAt);
    }

    /**
     * 적립한 포인트를 취소합니다.
     * 
     * <p>적립 포인트 상태가 정상 상태이고 잔액이 변경되지 않은 경우
     * 포인트 적립을 취소합니다.</p>
     */
    public void cancelEarn() {
        // 정상 상태가 아니면 취소할 수 없음
        if (status != EarnedPointStatus.ACTIVE) {
            throw new IllegalStateException("이미 사용되었거나 만료/취소된 적립은 취소할 수 없습니다.");
        }

        // 정상 상태가 아니면 취소할 수 없음
        if (amount != remainingAmount) {
            throw new IllegalStateException("일부가 사용되어 적립을 취소할 수 없습니다.");
        }

        // 남은 금액 0원, 취소 상태로 변경
        this.remainingAmount = 0;
        this.status = EarnedPointStatus.CANCELED;
    }

    /**
     * 적립한 포인트를 사용합니다.
     * @param amount 사용 금액
     * @return 실제 사용된 금액
     */
    public int use(int amount) {
        // 정상 상태가 아니면 사용할 수 없음
        if (this.status != EarnedPointStatus.ACTIVE) {
            throw new IllegalStateException("Not usable");
        }

        // 이 적립건에서 실제로 사용할 수 있는 금액 = 요청 vs 남은금액 중 작은 것
        int used = Math.min(amount, remainingAmount);

        // 남은 금액 = 남은 금액 - 사용 금액
        remainingAmount -= used;

        // 남은 금액 0 이면 사용 완료 상태로 변경
        if (remainingAmount == 0) {
            this.status = EarnedPointStatus.USED;
        }

        return used;
    }

    /**
     * 적립한 포인트의 금액을 원복합니다.
     * @param amount 원복할 금액
     */
    public void restore(int amount) {
        this.remainingAmount += amount;
        this.status = EarnedPointStatus.ACTIVE;
    }

    /**
     * 적립한 포인트를 만료합니다.
     * @param now 현재 LocalDateTime
     */
    public void expire(LocalDateTime now) {
        // 만료 일시가 현재보다 이전이면서 정상 상태의 포인트는 만료 처리
        if (expireAt.isBefore(now) && status == EarnedPointStatus.ACTIVE) {
            this.remainingAmount = 0;
            this.status = EarnedPointStatus.EXPIRED;
        }
    }

    public boolean isActive() {
        return status == EarnedPointStatus.ACTIVE;
    }
}

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

    /**
     * 적립 취소
     */
    public void cancelEarn() {
        // 정상 상태가 아니면 취소할 수 없음
        if (status != EarnedPointStatus.ACTIVE) {
            throw new IllegalStateException("이미 사용되었거나 만료/취소된 적립은 취소할 수 없습니다.");
        }

        // 남은 금액 0원, 취소 상태로 변경
        this.remainingAmount = 0;
        this.status = EarnedPointStatus.CANCELED;
    }

    /**
     * 사용
     * @param amount 사용 금액
     * @return 실제 사용된 금액
     */
    public int use(int amount) {
        // 정상 상태가 아니면 사용할 수 없음
        if (this.status != EarnedPointStatus.ACTIVE) {
            throw new IllegalStateException("Not usable");
        }
        // 사용하려는 금액이 남은 금액보다 크면 사용할 수 없음
        if (amount > remainingAmount) {
            throw new IllegalArgumentException("Not enough remaining");
        }

        // 이 적립건에서 실제로 사용할 수 있는 금액 = 요청 vs 남은금액 중 작은 것
        int used = Math.min(requestAmount, remainingAmount);

        // 남은 금액 = 남은 금액 - 사용 금액
        remainingAmount -= used;

        // 남은 금액 0 이면 사용 완료 상태로 변경
        if (remainingAmount == 0) {
            this.status = EarnedPointStatus.USED;
        }

        return used;
    }

    /**
     * 원복
     * @param amount 원복할 금액
     */
    public void restore(int amount) {
        this.remainingAmount += amount;
        this.status = EarnedPointStatus.ACTIVE;
    }

    /**
     * 만료
     * @param now 현재 LocalDateTime
     */
    public void expire(LocalDateTime now) {
        // 만료 일시가 현재보다 이전이면서 정상 상태의 포인트는 만료 처리
        if (expireAt.isBefore(now) && status == EarnedPointStatus.ACTIVE) {
            this.remainingAmount = 0;
            this.status = EarnedPointStatus.EXPIRED;
        }
    }

}

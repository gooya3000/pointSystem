package com.example.pointsystem.domain.wallet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용 요청이 어떤 적립 포인트에서 차감되었는지 나타내는 상세 정보입니다.
 */
@Getter
public class PointUsageDetail {

    private final Long earnedPointId;
    private int amount;

    public PointUsageDetail(Long earnedPointId, int amount) {
        this.earnedPointId = earnedPointId;
        this.amount = amount;
    }

    public static PointUsageDetail of(Long earnedPointId, int used) {
        return new PointUsageDetail(earnedPointId, used);
    }

    /**
     * 사용 취소 시 취소 가능한 금액을 차감합니다.
     * @param cancelAmount 취소 시도 금액
     * @return 실제로 차감된 금액
     */
    public int cancelUsage(int cancelAmount) {
        int cancelable = Math.min(cancelAmount, this.amount);
        this.amount -= cancelable;
        return cancelable;
    }
}

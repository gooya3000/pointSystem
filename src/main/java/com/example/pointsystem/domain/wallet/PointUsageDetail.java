package com.example.pointsystem.domain.wallet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용 요청이 어떤 적립 포인트에서 차감되었는지 나타내는 상세 정보입니다.
 */
@RequiredArgsConstructor
@Getter
public class PointUsageDetail {

    private final Long earnedPointId;
    private final int amount;

    public static PointUsageDetail of(Long earnedPointId, int used) {
        return new PointUsageDetail(earnedPointId, used);
    }
}

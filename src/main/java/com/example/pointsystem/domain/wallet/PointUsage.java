package com.example.pointsystem.domain.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 포인트 사용 기록과 상세 사용 내역을 보관하는 도메인 모델입니다.
 */
@RequiredArgsConstructor
@Getter
public class PointUsage {

    private final Long usageId;
    private final Long memberId;
    private final String orderNo;
    private int usedAmount;
    private final List<PointUsageDetail> details;
    private final List<PointUsageEvent> events;
    private final LocalDateTime createdAt;

    public PointUsage(Long usageId, Long memberId, String orderNo, int usedAmount, List<PointUsageDetail> details, LocalDateTime createdAt) {
        this.usageId = usageId;
        this.memberId = memberId;
        this.orderNo = orderNo;
        this.usedAmount = usedAmount;
        this.details = details;
        this.createdAt = createdAt;
        this.events = new ArrayList<>();
    }

    public static PointUsage createUse(Long memberId, String orderNo, int amount, List<PointUsageDetail> details) {
        int sum = details.stream().mapToInt(PointUsageDetail::getAmount).sum();
        if (sum != amount) {
            throw new IllegalArgumentException("사용 금액과 사용 상세 합계가 일치하지 않습니다.");
        }
        return new PointUsage(null, memberId, orderNo, amount, details, LocalDateTime.now());
    }

    public void addEvent(PointUsageEvent event) {
        this.events.add(event);
    }

    public List<PointUsageEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }

    /**
     * 사용 취소 시 금액과 상세 내역을 반영합니다.
     * @param cancelAmount 취소 금액
     * @param restoredDetails 환불된 적립 포인트 상세 내역
     */
    public void cancel(int cancelAmount, List<PointUsageDetail> restoredDetails) {
        if (cancelAmount <= 0) {
            throw new IllegalArgumentException("취소 금액은 0보다 커야 합니다.");
        }
        if (cancelAmount > usedAmount) {
            throw new IllegalArgumentException("취소 금액이 현재 사용 금액을 초과했습니다.");
        }

        this.usedAmount -= cancelAmount;

        if (this.usedAmount == 0) {
            addEvent(PointUsageEvent.useCanceled(cancelAmount, restoredDetails));
        } else {
            addEvent(PointUsageEvent.usePartiallyCanceled(cancelAmount, restoredDetails));
        }
    }
}

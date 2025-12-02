package com.example.pointsystem.domain.wallet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public void addEvent(PointUsageEvent event) {
        this.events.add(event);
    }

    public List<PointUsageEvent> getEvents() {
        return Collections.unmodifiableList(events);
    }
}

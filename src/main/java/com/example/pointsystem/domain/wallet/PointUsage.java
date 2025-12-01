package com.example.pointsystem.domain.wallet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class PointUsage {

    private final Long usageId;
    private final Long memberId;
    private final String orderNo;
    private int usedAmount;
    private final List<PointUsageDetail> usedDetails;
    private final List<PointUsageEvent> events;

}

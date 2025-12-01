package com.example.pointsystem.domain.wallet;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PointUsageEvent {

    private final PointUsageEventType eventType;  // USE, USE_CANCEL ...
    private final int amount;
    private final List<PointUsageDetail> details;
    private final LocalDateTime createdAt;

}

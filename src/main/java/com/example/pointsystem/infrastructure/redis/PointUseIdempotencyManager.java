package com.example.pointsystem.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

/**
 * 포인트 사용 요청의 멱등성 키를 Redis에 저장/조회합니다.
 */
@Component
@RequiredArgsConstructor
public class PointUseIdempotencyManager {

    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);
    private static final String KEY_PREFIX = "idempotency:point:use:";

    private final RedissonClient redissonClient;

    public Optional<Long> findUsageId(Long memberId, String orderNo) {
        RBucket<String> bucket = redissonClient.getBucket(key(memberId, orderNo));
        String value = bucket.get();

        if (value == null || value.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(Long.parseLong(value));
        } catch (NumberFormatException e) {
            bucket.delete();
            return Optional.empty();
        }
    }

    public void saveUsageId(Long memberId, String orderNo, Long usageId) {
        if (usageId == null) {
            return;
        }

        RBucket<String> bucket = redissonClient.getBucket(key(memberId, orderNo));
        bucket.set(String.valueOf(usageId), IDEMPOTENCY_TTL);
    }

    public void clear(Long memberId, String orderNo) {
        redissonClient.getBucket(key(memberId, orderNo)).delete();
    }

    private String key(Long memberId, String orderNo) {
        return KEY_PREFIX + memberId + ":" + orderNo;
    }
}

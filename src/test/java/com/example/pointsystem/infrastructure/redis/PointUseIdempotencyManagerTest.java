package com.example.pointsystem.infrastructure.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointUseIdempotencyManagerTest {

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RBucket<String> bucket;

    private PointUseIdempotencyManager manager;

    @BeforeEach
    void setUp() {
        manager = new PointUseIdempotencyManager(redissonClient);
    }

    @Test
    // 의도: 멱등 키가 없으면 빈 결과를 반환한다.
    void findUsageId_returns_empty_when_not_exists() {
        stubBucket();
        when(bucket.get()).thenReturn(null);

        Optional<Long> result = manager.findUsageId(1L, "ORDER-1");

        assertTrue(result.isEmpty());
    }

    @Test
    // 의도: 저장된 usageId 문자열을 Long으로 정상 복원한다.
    void findUsageId_returns_usage_id_when_numeric_value_exists() {
        stubBucket();
        when(bucket.get()).thenReturn("123");

        Optional<Long> result = manager.findUsageId(1L, "ORDER-1");

        assertTrue(result.isPresent());
        assertEquals(123L, result.get());
    }

    @Test
    // 의도: 손상된 값은 키를 삭제해 이후 요청이 정상 복구되도록 한다.
    void findUsageId_deletes_key_and_returns_empty_when_value_is_invalid() {
        stubBucket();
        when(bucket.get()).thenReturn("not-a-number");

        Optional<Long> result = manager.findUsageId(1L, "ORDER-1");

        assertTrue(result.isEmpty());
        verify(bucket).delete();
    }

    @Test
    // 의도: 신규 usageId 저장 시 TTL(24시간)을 함께 설정한다.
    void saveUsageId_stores_value_with_ttl() {
        stubBucket();
        manager.saveUsageId(2L, "ORDER-2", 987L);

        verify(bucket).set(eq("987"), eq(Duration.ofHours(24)));
    }

    @Test
    // 의도: usageId가 없는 경우 Redis write를 발생시키지 않는다.
    void saveUsageId_skips_when_usage_id_is_null() {
        manager.saveUsageId(2L, "ORDER-2", null);

        verify(redissonClient, never()).getBucket(any(String.class));
    }

    private void stubBucket() {
        when(redissonClient.<String>getBucket(any(String.class))).thenReturn(bucket);
    }
}

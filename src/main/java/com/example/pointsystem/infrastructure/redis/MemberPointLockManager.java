package com.example.pointsystem.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 회원 단위 포인트 갱신 연산에 대한 Redis 분산 락을 제공합니다.
 */
@Component
@RequiredArgsConstructor
public class MemberPointLockManager {

    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(2);
    private static final String LOCK_KEY_PREFIX = "lock:point:member:";
    private final RedissonClient redissonClient;

    public void executeWithLock(Long memberId, Runnable action) {
        Objects.requireNonNull(action, "action 은 비어 있을 수 없습니다.");
        executeWithLock(memberId, () -> {
            action.run();
            return null;
        });
    }

    public <T> T executeWithLock(Long memberId, Supplier<T> action) {
        Objects.requireNonNull(memberId, "회원 식별자는 비어 있을 수 없습니다.");
        Objects.requireNonNull(action, "action 은 비어 있을 수 없습니다.");

        String lockKey = LOCK_KEY_PREFIX + memberId;
        RLock lock = redissonClient.getLock(lockKey);
        boolean locked;

        try {
            locked = lock.tryLock(WAIT_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // InterruptedException은 스레드가 어떤 작업을 기다리고 있을 때 다른 스레드가 "그만 기다려!"라고 interrupt()를 호출하면 발생하는 예외
            // 예시: 서버 종료, 요청 취소, ThreadPool 종료, Future.cancel()
            // Java는 InterruptedException이 발생하면 인터럽트 상태를 자동으로 지워버리기 때문에 인터럽트 상태를 다시 true로 복구
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 대기 중 인터럽트가 발생했습니다.", e);
        }

        if (!locked) { //타임아웃 시 락 획득 실패
            throw new IllegalStateException("요청이 많아 잠시 처리할 수 없습니다. 잠시 후 다시 시도해 주세요.");
        }

        try {
            return action.get();
        } finally {
            if (lock.isHeldByCurrentThread()) { // 현재 스레드가 락을 가지고 있을 때만 unlock
                lock.unlock();
            }
        }
    }
}

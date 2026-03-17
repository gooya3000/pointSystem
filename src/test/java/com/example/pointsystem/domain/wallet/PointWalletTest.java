package com.example.pointsystem.domain.wallet;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PointWalletTest {

    @Test
    void expire_marks_active_points_and_returns_expired_points() {
        LocalDateTime now = LocalDateTime.now();

        EarnedPoint expiredTarget = new EarnedPoint(
                1L,
                1000,
                1000,
                now.minusMinutes(1),
                EarnedPointSourceType.NORMAL,
                EarnedPointStatus.ACTIVE,
                now.minusDays(1)
        );
        EarnedPoint notExpired = new EarnedPoint(
                2L,
                500,
                500,
                now.plusMinutes(1),
                EarnedPointSourceType.NORMAL,
                EarnedPointStatus.ACTIVE,
                now.minusDays(1)
        );

        PointWallet wallet = new PointWallet(10L, new ArrayList<>(List.of(expiredTarget, notExpired)));

        List<EarnedPoint> expired = wallet.expire(now);

        assertEquals(1, expired.size());
        assertEquals(1L, expired.get(0).getEarnedPointId());
        assertEquals(EarnedPointStatus.EXPIRED, expiredTarget.getStatus());
        assertEquals(0, expiredTarget.getRemainingAmount());
        assertEquals(EarnedPointStatus.ACTIVE, notExpired.getStatus());
    }

    @Test
    void expire_works_when_expire_at_equals_now() {
        LocalDateTime now = LocalDateTime.now();
        EarnedPoint boundary = new EarnedPoint(
                3L,
                200,
                200,
                now,
                EarnedPointSourceType.NORMAL,
                EarnedPointStatus.ACTIVE,
                now.minusDays(1)
        );

        PointWallet wallet = new PointWallet(20L, new ArrayList<>(List.of(boundary)));

        List<EarnedPoint> expired = wallet.expire(now);

        assertEquals(1, expired.size());
        assertEquals(EarnedPointStatus.EXPIRED, boundary.getStatus());
    }

    @Test
    void expire_throws_when_now_is_null() {
        PointWallet wallet = PointWallet.createWallet(30L);
        assertThrows(NullPointerException.class, () -> wallet.expire(null));
    }
}

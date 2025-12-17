package com.example.pointsystem.domain.wallet;

import com.example.pointsystem.domain.policy.PointPolicy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

/**
 * 회원별 포인트 적립과 사용을 집계하는 지갑 도메인 모델입니다.
 */
@RequiredArgsConstructor
@Getter
public class PointWallet {

    private final Long memberId;
    private final List<EarnedPoint> earnedPoints;

    /**
     * 지갑에 포인트를 적립합니다.
     * @param amount 금액
     * @param expireAt 만료일시
     * @param sourceType 적립 포인트 발생 유형
     * @param policy 포인트 정책
     * @return EarnedPoint
     */
    public EarnedPoint earn(int amount, LocalDateTime expireAt, EarnedPointSourceType sourceType, PointPolicy policy) {
        // 1) 1회 최대 적립 검증
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        if (amount > policy.getMaxEarnPerTxn()) {
            throw new IllegalArgumentException(
                    "Amount exceeds maximum points per earning transaction.");
        }

        // 2) 최대 보유 포인트 검증
        int currentBalance = calculateCurrentBalance();
        if (currentBalance + amount > policy.getMaxBalance()) {
            throw new IllegalArgumentException(
                    "Amount exceeds maximum allowed point balance.");
        }

        // 3) EarnedPoint 생성 (여기서 만료일 기본 365일 처리)
        EarnedPoint earnedPoint = EarnedPoint.createPoint(null, amount, amount, expireAt, sourceType, EarnedPointStatus.ACTIVE, LocalDateTime.now());

        this.earnedPoints.add(earnedPoint);

        return earnedPoint;
    }

    // 적립 취소
    public EarnedPoint cancelEarn(long earnedPointId) {
        throw new UnsupportedOperationException("PointWallet.cancelEarn 아직 구현 안 됨");
    }

    /**
     * 적립한 포인트를 사용합니다.
     * @param amount 금액
     * @param orderNo 주문번호
     * @return PointUsage
     */
    public PointUsage use(int amount, String orderNo) {
        int remaining = amount;
        List<PointUsageDetail> details = new ArrayList<>();

        for (EarnedPoint ep : earnedPoints.stream()
                .filter(EarnedPoint::isActive)
                .sorted(
                        comparing((EarnedPoint ep) -> ep.getSourceType() == EarnedPointSourceType.ADMIN ? 0 : 1)
                                .thenComparing(EarnedPoint::getExpireAt)
                )
                .toList()) {

            if (remaining == 0) break;

            int used = ep.use(remaining);

            details.add(PointUsageDetail.of(ep.getEarnedPointId(), used));
            remaining -= used;
        }

        if (remaining > 0) {
            throw new IllegalStateException("Not enough points");
        }

        PointUsage usage = PointUsage.createUse(
                memberId,
                orderNo,
                amount,
                details
        );

        usage.addEvent(PointUsageEvent.useCreated(amount, details));

        return usage;
    }

    // 부분 취소
    public PointUsage cancelUse(long usageId, int cancelAmount) {
        throw new UnsupportedOperationException("PointWallet.cancelUse 아직 구현 안 됨");
    }

    // 전체 취소
    public PointUsage cancelUseAll(long usageId) {
        throw new UnsupportedOperationException("PointWallet.cancelUseAll 아직 구현 안 됨");
    }

    // 만료 처리
    public List<EarnedPoint> expire(LocalDateTime now) {
        throw new UnsupportedOperationException("PointWallet.expire 아직 구현 안 됨");
    }

    // 지갑 생성
    public static PointWallet createWallet(Long memberId) {
        return new PointWallet(memberId, new ArrayList<>());
    }

    // 잔액 계산
    public int calculateCurrentBalance() {
        return earnedPoints.stream()
                .mapToInt(EarnedPoint::getRemainingAmount)
                .sum();
    }

}

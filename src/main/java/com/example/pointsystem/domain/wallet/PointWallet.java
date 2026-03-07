package com.example.pointsystem.domain.wallet;

import com.example.pointsystem.domain.policy.PointPolicy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            throw new IllegalArgumentException("적립 금액은 0보다 커야 합니다.");
        }
        if (amount > policy.getMaxEarnPerTxn()) {
            throw new IllegalArgumentException(
                    "1회 적립 한도를 초과했습니다.");
        }

        // 2) 최대 보유 포인트 검증
        int currentBalance = calculateCurrentBalance();
        if (currentBalance + amount > policy.getMaxBalance()) {
            throw new IllegalArgumentException(
                    "최대 보유 가능 포인트를 초과했습니다.");
        }

        // 3) EarnedPoint 생성 (여기서 만료일 기본 365일 처리)
        EarnedPoint earnedPoint = EarnedPoint.createPoint(null, amount, amount, expireAt, sourceType, EarnedPointStatus.ACTIVE, LocalDateTime.now());

        this.earnedPoints.add(earnedPoint);

        return earnedPoint;
    }

    // 적립 취소
    public EarnedPoint cancelEarn(long earnedPointId) {
        EarnedPoint earnedPoint = findEarnedPoint(earnedPointId);
        earnedPoint.cancelEarn();
        return earnedPoint;
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
            throw new IllegalStateException("사용 가능한 포인트가 부족합니다.");
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

    public PointUsage cancelUseAll(PointUsage usage) {
        return cancelUse(usage, usage.getUsedAmount());
    }

    /**
     * 사용을 부분/전체 취소합니다.
     * @param usage 취소 대상 사용 내역
     * @param cancelAmount 취소 금액
     * @return 취소 후 사용 내역
     */
    public PointUsage cancelUse(PointUsage usage, int cancelAmount) {
        if (!usage.getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("취소 권한이 없는 사용자입니다.");
        }

        int remaining = cancelAmount;
        List<PointUsageDetail> restoredDetails = new ArrayList<>();

        for (PointUsageDetail detail : usage.getDetails()) {
            if (remaining == 0) break;

            EarnedPoint earnedPoint = findEarnedPoint(detail.getEarnedPointId());
            int canceled = detail.cancelUsage(remaining);

            if (canceled > 0) {
                earnedPoint.restore(canceled);
                restoredDetails.add(PointUsageDetail.of(earnedPoint.getEarnedPointId(), canceled));
                remaining -= canceled;
            }
        }

        if (remaining > 0) {
            throw new IllegalArgumentException("취소 금액이 사용 금액을 초과했습니다.");
        }

        usage.cancel(cancelAmount, restoredDetails);
        return usage;
    }

    // 만료 처리
    public List<EarnedPoint> expire(LocalDateTime now) {
        Objects.requireNonNull(now, "현재 시각은 비어 있을 수 없습니다.");

        List<EarnedPoint> expiredPoints = new ArrayList<>();
        for (EarnedPoint earnedPoint : earnedPoints) {
            int beforeRemainingAmount = earnedPoint.getRemainingAmount();
            EarnedPointStatus beforeStatus = earnedPoint.getStatus();

            earnedPoint.expire(now);

            if (beforeRemainingAmount > 0
                    && beforeStatus == EarnedPointStatus.ACTIVE
                    && earnedPoint.getStatus() == EarnedPointStatus.EXPIRED) {
                expiredPoints.add(earnedPoint);
            }
        }

        return expiredPoints;
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

    private EarnedPoint findEarnedPoint(Long earnedPointId) {
        return earnedPoints.stream()
                .filter(ep -> ep.getEarnedPointId().equals(earnedPointId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("적립 포인트를 찾을 수 없습니다."));
    }

}

package com.example.pointsystem.application.wallet;

import com.example.pointsystem.application.policy.PointPolicyService;
import com.example.pointsystem.domain.policy.PointPolicy;
import com.example.pointsystem.domain.wallet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 회원 포인트 지갑의 적립, 사용, 조회 기능을 제공하는 응용 서비스입니다.
 */
@Service
@RequiredArgsConstructor
public class PointWalletService {

    private final PointWalletRepository pointWalletRepository;
    private final PointUsageRepository pointUsageRepository;
    private final PointPolicyService pointPolicyService;

    /**
     * 포인트를 적립합니다.
     * @param memberId 회원 ID
     * @param amount 금액
     * @param expireAt 만료일시
     * @param sourceType 적립 포인트 발생 유형
     */
    @Transactional
    public void earnPoint(Long memberId, int amount, LocalDateTime expireAt, String sourceType) {

        // 1회 적립 한도, 최대 보유 한도 정책 가져오기
        PointPolicy policy = pointPolicyService.getCurrentPolicy();

        PointWallet wallet = pointWalletRepository.findByMemberId(memberId)
                .orElseGet(() -> PointWallet.createWallet(memberId));

        wallet.earn(amount, expireAt, EarnedPointSourceType.fromNullable(sourceType), policy);

        pointWalletRepository.save(wallet);
    }

    /**
     * 지갑을 조회합니다.
     * @param memberId 회원 ID
     * @return PointWallet
     */
    @Transactional(readOnly = true)
    public PointWallet getWallet(Long memberId) {
        return pointWalletRepository.findByMemberId(memberId)
                .orElseGet(() -> PointWallet.createWallet(memberId));
    }

    /**
     * 포인트를 사용합니다.
     * @param memberId 회원 ID
     * @param amount 금액
     * @param orderNo 주문번호
     * @return PointUsage
     */
    @Transactional
    public PointUsage usePoint(Long memberId, int amount, String orderNo) {
        PointWallet wallet = pointWalletRepository.findByMemberId(memberId)
                .orElseGet(() -> PointWallet.createWallet(memberId));

        PointUsage usage = wallet.use(amount, orderNo); // 도메인 호출

        pointWalletRepository.save(wallet);
        return pointUsageRepository.save(usage);
    }

    /**
     * 적립을 취소합니다.
     */
    @Transactional
    public void cancelEarn(Long memberId, Long earnedPointId) {
        PointWallet wallet = pointWalletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for member " + memberId));

        wallet.cancelEarn(earnedPointId);
        pointWalletRepository.save(wallet);
    }

    /**
     * 포인트 사용을 부분 취소합니다.
     */
    @Transactional
    public PointUsage cancelUse(Long memberId, Long usageId, int cancelAmount) {
        PointWallet wallet = pointWalletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for member " + memberId));

        PointUsage usage = pointUsageRepository.findById(usageId)
                .orElseThrow(() -> new IllegalArgumentException("Usage not found: " + usageId));

        PointUsage canceled = wallet.cancelUse(usage, cancelAmount);

        pointWalletRepository.save(wallet);
        return pointUsageRepository.save(canceled);
    }

    /**
     * 포인트 사용을 전액 취소합니다.
     */
    @Transactional
    public PointUsage cancelUseAll(Long memberId, Long usageId) {
        PointWallet wallet = pointWalletRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for member " + memberId));

        PointUsage usage = pointUsageRepository.findById(usageId)
                .orElseThrow(() -> new IllegalArgumentException("Usage not found: " + usageId));

        PointUsage canceled = wallet.cancelUseAll(usage);

        pointWalletRepository.save(wallet);
        return pointUsageRepository.save(canceled);
    }



}

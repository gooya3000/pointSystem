package com.example.pointsystem.application.wallet;

import com.example.pointsystem.application.policy.PointPolicyService;
import com.example.pointsystem.domain.policy.PointPolicy;
import com.example.pointsystem.domain.wallet.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointWalletService {

    private final PointWalletRepository pointWalletRepository;
    private final PointUsageRepository pointUsageRepository;
    private final PointPolicyService pointPolicyService;

    @Transactional
    public PointUsage usePoint(Long memberId, int amount, String orderNo) {
        PointWallet wallet = pointWalletRepository.findByMemberId(memberId)
                .orElseGet(() -> PointWallet.createWallet(memberId));

        PointUsage usage = wallet.use(amount, orderNo); // 도메인 호출

        pointWalletRepository.save(wallet);
        return pointUsageRepository.save(usage);
    }

    @Transactional
    public void earnPoint(Long memberId, int amount, LocalDateTime expireAt, EarnedPointSourceType sourceType) {

        // 1회 적립 한도, 최대 보유 한도 정책 가져오기
        PointPolicy policy = pointPolicyService.getCurrentPolicy();

        PointWallet wallet = pointWalletRepository.findByMemberId(memberId)
                .orElseGet(() -> PointWallet.createWallet(memberId));

        wallet.earn(amount, expireAt, sourceType, policy);

        pointWalletRepository.save(wallet);
    }

    /**
     * 지갑 조회
     */
    @Transactional(readOnly = true)
    public PointWallet getWallet(Long memberId) {
        return pointWalletRepository.findByMemberId(memberId)
                .orElseGet(() -> PointWallet.createWallet(memberId));
    }

}

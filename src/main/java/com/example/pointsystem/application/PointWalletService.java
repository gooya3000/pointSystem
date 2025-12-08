package com.example.pointsystem.application;

import com.example.pointsystem.domain.wallet.PointUsage;
import com.example.pointsystem.domain.wallet.PointUsageRepository;
import com.example.pointsystem.domain.wallet.PointWallet;
import com.example.pointsystem.domain.wallet.PointWalletRepository;
import com.example.pointsystem.infrastructure.jpa.wallet.PointWalletMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class PointWalletService {

    private final PointWalletRepository pointWalletRepository;
    private final PointUsageRepository pointUsageRepository;

    @Transactional
    public PointUsage usePoint(Long memberId, int amount, String orderNo) {
        PointWallet wallet = pointWalletRepository.findByMemberId(memberId)
                .orElseGet(() -> new PointWallet(memberId, new ArrayList<>()));

        PointUsage usage = wallet.use(amount, orderNo); // 도메인 호출

        pointWalletRepository.save(wallet);
        return pointUsageRepository.save(usage);
    }
}

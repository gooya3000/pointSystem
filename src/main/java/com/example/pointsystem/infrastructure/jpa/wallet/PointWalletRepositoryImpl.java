package com.example.pointsystem.infrastructure.jpa.wallet;

import com.example.pointsystem.domain.wallet.PointWallet;
import com.example.pointsystem.domain.wallet.PointWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 포인트 지갑 도메인 저장소의 JPA 구현체입니다.
 */
@Repository
@RequiredArgsConstructor
public class PointWalletRepositoryImpl implements PointWalletRepository {

    private final PointWalletJpaRepository jpaRepository;
    private final PointWalletMapper mapper;

    @Override
    public Optional<PointWallet> findByMemberId(Long memberId) {
        return jpaRepository.findById(memberId).map(mapper::toDomain);
    }

    @Override
    public PointWallet save(PointWallet wallet) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(wallet)));
    }
}

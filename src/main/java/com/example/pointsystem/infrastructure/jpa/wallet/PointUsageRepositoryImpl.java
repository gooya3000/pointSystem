package com.example.pointsystem.infrastructure.jpa.wallet;


import com.example.pointsystem.domain.wallet.PointUsage;
import com.example.pointsystem.domain.wallet.PointUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * PointUsageRepository 의 JPA 기반 구현체입니다.
 */
@Repository
@RequiredArgsConstructor
public class PointUsageRepositoryImpl implements PointUsageRepository {

    private final PointUsageJpaRepository jpaRepository;
    private final PointUsageMapper mapper;

    @Override
    public PointUsage save(PointUsage usage) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(usage)));
    }

    @Override
    public Optional<PointUsage> findById(Long usageId) {
        return jpaRepository.findById(usageId)
                .map(mapper::toDomain);
    }

}

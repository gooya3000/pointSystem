package com.example.pointsystem.infrastructure.jpa.wallet;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PointUsageEntity 에 대한 Spring Data JPA 저장소입니다.
 */
public interface PointUsageJpaRepository extends JpaRepository<PointUsageEntity, Long> {
}

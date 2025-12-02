package com.example.pointsystem.infrastructure.jpa.wallet;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PointWalletEntity 에 대한 Spring Data JPA 저장소입니다.
 *
 * <p>도메인 레이어에서는 이 타입을 직접 사용하지 않고,
 * 어댑터 역할을 하는 PointWalletRepositoryImpl 을 통해 접근합니다.</p>
 */
public interface PointWalletJpaRepository extends JpaRepository<PointWalletEntity, Long> {
}

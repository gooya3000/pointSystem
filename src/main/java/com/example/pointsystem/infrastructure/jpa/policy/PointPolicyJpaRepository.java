package com.example.pointsystem.infrastructure.jpa.policy;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 포인트 정책 엔티티를 관리하는 Spring Data JPA 리포지토리입니다.
 */
public interface PointPolicyJpaRepository extends JpaRepository<PointPolicyEntity, Long> {
}

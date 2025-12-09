package com.example.pointsystem.infrastructure.jpa.policy;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PointPolicyJpaRepository extends JpaRepository<PointPolicyEntity, Long> {
}

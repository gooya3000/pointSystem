package com.example.pointsystem.application.policy;

import com.example.pointsystem.domain.policy.PointPolicy;
import com.example.pointsystem.infrastructure.jpa.policy.PointPolicyEntity;
import com.example.pointsystem.infrastructure.jpa.policy.PointPolicyJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointPolicyService {

    private static final long DEFAULT_POLICY_ID = 1L;

    private final PointPolicyJpaRepository jpaRepository;

    public PointPolicy getCurrentPolicy() {
        PointPolicyEntity entity = jpaRepository.findById(DEFAULT_POLICY_ID)
                .orElseThrow(() -> new IllegalStateException("Point policy not configured."));

        return new PointPolicy(
                entity.getMaxEarnPerTxn(),
                entity.getMaxBalance()
        );
    }

}

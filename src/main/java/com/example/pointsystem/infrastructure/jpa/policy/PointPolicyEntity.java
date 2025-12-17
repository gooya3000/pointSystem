package com.example.pointsystem.infrastructure.jpa.policy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 포인트 정책 설정을 저장하는 JPA 엔티티입니다.
 */
@Entity
@Table(name = "point_policy")
@Getter
@NoArgsConstructor
public class PointPolicyEntity {

    @Id
    @Column(name = "policy_id")
    private Long id;

    @Column(name = "max_earn_per_txn", nullable = false)
    private int maxEarnPerTxn;

    @Column(name = "max_balance", nullable = false)
    private int maxBalance;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

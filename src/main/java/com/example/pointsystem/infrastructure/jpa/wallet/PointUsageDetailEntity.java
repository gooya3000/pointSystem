package com.example.pointsystem.infrastructure.jpa.wallet;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 포인트 사용 상세 정보를 저장하는 JPA 엔티티입니다.
 */
@Entity
@Data
@Table(name = "point_usage_detail")
@NoArgsConstructor
public class PointUsageDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usage_id")
    private PointUsageEntity usage;

    @Column(name = "earned_point_id", nullable = false)
    private Long earnedPointId;   // EarnedPoint FK (연관 안 맺고 ID만 들고 있게 설계)

    @Column(nullable = false)
    private int amount;

    /**
     * 특정 적립 포인트를 기반으로 사용된 상세 내역(PointUsageDetailEntity)을 생성합니다.
     *
     * <p>포인트 사용은 여러 적립 포인트에서 나누어 차감될 수 있으므로,
     * 각 적립 포인트별 차감 금액을 별도로 기록하는 것이 필요합니다.
     * 이 생성자는 해당 상세 내역을 유효한 상태로 생성하기 위해
     * earnedPointId와 amount 값을 필수로 받습니다.</p>
     *
     * <p>신규 사용 상세 내역을 저장할 때 사용되며,
     * JPA는 조회 시 기본 생성자를 사용합니다.</p>
     *
     * @param earnedPointId 차감된 적립 포인트 ID
     * @param amount 해당 적립 포인트에서 차감된 금액
     */
    public PointUsageDetailEntity(Long earnedPointId, int amount) {
        this.earnedPointId = earnedPointId;
        this.amount = amount;
    }
}

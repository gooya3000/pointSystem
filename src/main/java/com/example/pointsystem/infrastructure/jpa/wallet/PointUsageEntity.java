package com.example.pointsystem.infrastructure.jpa.wallet;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "point_usage")
@NoArgsConstructor
public class PointUsageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;         // 지갑과 직접 연관 안 맺고, memberId만 저장하는 설계도 가능

    @Column(name = "order_no", length = 50, nullable = false)
    private String orderNo;

    @Column(name = "used_amount", nullable = false)
    private int usedAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "usage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PointUsageDetailEntity> details = new ArrayList<>();

    public void addDetail(PointUsageDetailEntity detail) {
        this.details.add(detail);
        detail.setUsage(this);
    }

    /**
     * 새로운 포인트 사용 내역(PointUsageEntity)을 생성합니다.
     *
     * <p>포인트 사용 기록은 반드시 사용 회원(memberId), 주문 번호(orderNo),
     * 사용 금액(usedAmount), 사용 시각(createdAt) 등의 정보를 포함해야 하므로,
     * 이 생성자는 해당 값들이 반드시 설정되도록 강제합니다.</p>
     *
     * <p>주로 포인트 사용 이벤트를 기록할 때 사용되며,
     * JPA는 엔티티 조회 시 기본 생성자를 사용합니다.</p>
     *
     * @param memberId 포인트를 사용한 회원의 식별자
     * @param orderNo 포인트가 사용된 주문 번호
     * @param usedAmount 총 사용 금액
     * @param createdAt 사용 발생 시각
     */
    public PointUsageEntity(Long memberId, String orderNo, int usedAmount, LocalDateTime createdAt, List<PointUsageDetailEntity> details) {
        this.memberId = memberId;
        this.orderNo = orderNo;
        this.usedAmount = usedAmount;
        this.createdAt = createdAt;
        this.details = details;
    }
}

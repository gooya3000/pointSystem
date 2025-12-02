package com.example.pointsystem.infrastructure.jpa.wallet;

import com.example.pointsystem.domain.wallet.EarnedPointSourceType;
import com.example.pointsystem.domain.wallet.EarnedPointStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "earned_point")
@NoArgsConstructor
@AllArgsConstructor
public class EarnedPointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "earned_point_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private PointWalletEntity pointWallet;

    @Column(nullable = false)
    private int amount;

    @Column(name = "remaining_amount", nullable = false)
    private int remainingAmount;

    @Column(name = "expire_at", nullable = false)
    private LocalDateTime expireAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private EarnedPointSourceType sourceType; // enum -> String 저장

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EarnedPointStatus status;     // enum -> String 저장

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 새로운 적립 포인트(EarnedPointEntity)를 생성합니다.
     *
     * <p>적립 포인트는 반드시 특정 지갑(wallet)에 속해야 하며,
     * 적립 금액, 잔여 금액, 만료일, 적립 유형, 상태와 같은 정보는
     * 적립 발생 시점에 모두 확정되어야 합니다.
     * 이 생성자는 이러한 도메인 규칙을 준수하도록 강제합니다.</p>
     *
     * <p>이 생성자는 도메인 모델을 JPA 엔티티로 변환할 때 또는
     * 새로운 적립 이벤트를 저장할 때 사용됩니다.
     * 데이터베이스에서 엔티티를 조회할 때는 JPA가 기본 생성자를 사용합니다.</p>
     *
     * @param pointWallet 적립 포인트가 귀속되는 지갑 엔티티
     * @param amount 최초 적립 금액
     * @param remainingAmount 최초 잔여 금액
     * @param expireAt 만료 일시
     * @param sourceType 적립 유형(예: ADMIN, PURCHASE 등)
     * @param status 초기 상태
     * @param createdAt 적립 발생 시각
     */
    public EarnedPointEntity(PointWalletEntity pointWallet, int amount, int remainingAmount, LocalDateTime expireAt, EarnedPointSourceType sourceType, EarnedPointStatus status, LocalDateTime createdAt) {
        this.pointWallet = pointWallet;
        this.amount = amount;
        this.remainingAmount = remainingAmount;
        this.expireAt = expireAt;
        this.sourceType = sourceType;
        this.status = status;
        this.createdAt = createdAt;
    }
}

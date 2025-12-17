package com.example.pointsystem.infrastructure.jpa.wallet;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 회원의 포인트 지갑을 표현하는 JPA 엔티티입니다.
 */
@Entity
@Getter
@Table(name = "point_wallet")
@NoArgsConstructor
public class PointWalletEntity {

    @Id
    @Column(name = "member_id")
    private Long memberId;

    @OneToMany(mappedBy = "pointWallet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EarnedPointEntity> earnedPoints = new ArrayList<>();

    /**
     * 새로운 회원 포인트 지갑(PointWalletEntity)을 생성합니다.
     *
     * <p>지갑은 반드시 특정 회원에게 귀속되어야 하므로,
     * 생성 시점에 memberId가 반드시 제공되어야 합니다.
     * 이는 "지갑은 회원 없이 존재할 수 없다"는 도메인 규칙을
     * 코드 차원에서 보장하기 위한 것입니다.</p>
     *
     * <p>이 생성자는 애플리케이션 로직에서 새로운 지갑을 만들 때 사용됩니다.
     * 데이터베이스에서 엔티티를 조회할 때는 JPA가 기본 생성자를 사용합니다.</p>
     *
     * @param memberId 지갑을 소유하는 회원의 고유 식별자
     */
    protected PointWalletEntity(Long memberId) { // final + @RequiredArgsConstructor 로 초기화하지 않는 이유는 [JPA는 기본 생성자 + 리플렉션] 으로 객체를 만든다. 따라서 final 사용할 수 없고 @RequiredArgsConstructor 사용 시 기본 생성자 가려버림.
        this.memberId = memberId;
    }

    /**
     * 포인트 적립
     * @param earnedPoint 적립 포인트
     */
    public void addEarnedPoint(EarnedPointEntity earnedPoint) {
        this.earnedPoints.add(earnedPoint);
        earnedPoint.setPointWallet(this);
    }
}

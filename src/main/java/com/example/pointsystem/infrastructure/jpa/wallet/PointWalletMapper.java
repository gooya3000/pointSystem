package com.example.pointsystem.infrastructure.jpa.wallet;

import com.example.pointsystem.domain.wallet.EarnedPoint;
import com.example.pointsystem.domain.wallet.PointWallet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 포인트 지갑 도메인과 JPA 엔티티 간의 변환을 수행합니다.
 */
@Component
public class PointWalletMapper {

    /**
     * PointWalletEntity -> PointWallet (도메인) 으로 변환합니다.
     */
    public PointWallet toDomain(PointWalletEntity entity) {
        List<EarnedPoint> earnedPoints = new ArrayList<>();
        if (entity.getEarnedPoints() != null) {
            for (EarnedPointEntity ep : entity.getEarnedPoints()) {
                earnedPoints.add(toDomain(ep));
            }
        }
        return new PointWallet(entity.getMemberId(), earnedPoints);
    }

    /**
     * EarnedPointEntity -> EarnedPoint (도메인) 으로 변환합니다.
     */
    private EarnedPoint toDomain(EarnedPointEntity entity) {
        return new EarnedPoint(
                entity.getId(),
                entity.getAmount(),
                entity.getRemainingAmount(),
                entity.getExpireAt(),
                entity.getSourceType(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    /**
     * PointWallet (도메인) -> PointWalletEntity 로 변환합니다.
     *
     * <p>간단한 구현으로는, 기존 자식 컬렉션을 모두 비우고
     * 도메인 기준으로 다시 구성하는 방식으로 시작할 수 있습니다.
     * (포트폴리오 첫 버전에서는 이 정도로도 충분합니다.)</p>
     */
    public PointWalletEntity toEntity(PointWallet wallet) {
        PointWalletEntity entity = new PointWalletEntity(wallet.getMemberId());

        if (wallet.getEarnedPoints() != null) {
            for (EarnedPoint ep : wallet.getEarnedPoints()) {
                EarnedPointEntity epEntity = toEntity(ep, entity);
                entity.getEarnedPoints().add(epEntity);
            }
        }
        return entity;
    }

    /**
     * EarnedPoint (도메인) -> EarnedPointEntity 로 변환합니다.
     */
    private EarnedPointEntity toEntity(EarnedPoint ep, PointWalletEntity walletEntity) {

        return new EarnedPointEntity(
                ep.getEarnedPointId(),
                walletEntity,
                ep.getAmount(),
                ep.getRemainingAmount(),
                ep.getExpireAt(),
                ep.getSourceType(),
                ep.getStatus(),
                ep.getCreatedAt(),
                null
        );
    }

}

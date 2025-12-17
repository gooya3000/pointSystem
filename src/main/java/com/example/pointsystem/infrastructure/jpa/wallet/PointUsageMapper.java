package com.example.pointsystem.infrastructure.jpa.wallet;

import com.example.pointsystem.domain.wallet.PointUsage;
import com.example.pointsystem.domain.wallet.PointUsageDetail;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 포인트 사용 도메인 객체와 JPA 엔티티 간 변환을 담당합니다.
 */
@Component
public class PointUsageMapper {

    public PointUsage toDomain(PointUsageEntity entity) {
        List<PointUsageDetail> details = new ArrayList<>();
        if (entity.getDetails() != null) {
            entity.getDetails().forEach(detailEntity -> {
                details.add(new PointUsageDetail(
                        detailEntity.getEarnedPointId(),
                        detailEntity.getAmount()
                ));
            });
        }

        return new PointUsage(
                entity.getId(),
                entity.getMemberId(),
                entity.getOrderNo(),
                entity.getUsedAmount(),
                details,
                entity.getCreatedAt()
        );
    }

    public PointUsageEntity toEntity(PointUsage usage) {
        PointUsageEntity entity = new PointUsageEntity(
                usage.getMemberId(),
                usage.getOrderNo(),
                usage.getUsedAmount(),
                usage.getCreatedAt()
        );

        if (usage.getDetails() != null) {
            usage.getDetails().forEach(detail -> {
                PointUsageDetailEntity detailEntity =
                        new PointUsageDetailEntity(detail.getEarnedPointId(), detail.getAmount());
                entity.addDetail(detailEntity);
            });
        }

        return entity;
    }

}

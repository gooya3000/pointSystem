package com.example.pointsystem.domain.wallet;

import java.util.Optional;

/**
 * PointUsage 에 대한 저장소 인터페이스입니다.
 */
public interface PointUsageRepository {

    /**
     * 포인트 사용 내역을 저장합니다.
     *
     * @param usage 포인트 사용 도메인 객체
     * @return 저장 후 최신 상태의 사용 도메인 객체
     */
    PointUsage save(PointUsage usage);

    /**
     * 사용 ID 기준으로 사용 내역을 조회합니다.
     *
     * @param usageId 사용 ID
     * @return 포인트 사용 도메인 객체 (없을 경우 Optional.empty)
     */
    Optional<PointUsage> findById(Long usageId);

}

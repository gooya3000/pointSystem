package com.example.pointsystem.domain.wallet;

import java.util.Optional;

/**
 * PointWallet(Aggregate Root) 에 대한 저장소 인터페이스입니다.
 *
 * <p>도메인 레이어에서 사용하는 순수 인터페이스이며,
 * 실제 구현체는 infrastructure(jpa 등) 레이어에서 제공합니다.</p>
 */
public interface PointWalletRepository {

    /**
     * 회원 ID 기준으로 지갑을 조회합니다.
     *
     * @param memberId 회원 ID
     * @return 지갑 도메인 객체 (없을 경우 Optional.empty)
     */
    Optional<PointWallet> findByMemberId(Long memberId);

    /**
     * 지갑을 저장합니다.
     *
     * <p>적립/사용 등으로 인해 EarnedPoint 목록이 변경된 상태를 그대로 저장합니다.</p>
     *
     * @param wallet 저장할 지갑 도메인 객체
     * @return 저장 후 최신 상태의 지갑 도메인 객체
     */
    PointWallet save(PointWallet wallet);

}

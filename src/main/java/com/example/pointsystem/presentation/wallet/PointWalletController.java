package com.example.pointsystem.presentation.wallet;

import com.example.pointsystem.application.wallet.PointWalletService;
import com.example.pointsystem.domain.wallet.PointUsage;
import com.example.pointsystem.domain.wallet.PointWallet;
import com.example.pointsystem.presentation.wallet.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/point")
public class PointWalletController {

    private final PointWalletService pointWalletService;

    /**
     * 포인트 적립
     * POST /api/point/{memberId}/earn
     */
    @PostMapping("/{memberId}/earn")
    public ResponseEntity<Void> earnPoint(
            @PathVariable Long memberId,
            @Valid @RequestBody EarnPointRequest request
    ) {
        pointWalletService.earnPoint(
                memberId,
                request.amount(),
                request.expireAt(),
                request.sourceType()
        );

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 지갑 조회
     * GET /api/point/{memberId}
     */
    @GetMapping("/{memberId}")
    public ResponseEntity<PointWalletResponse> getWallet(@PathVariable Long memberId) {
        PointWallet wallet = pointWalletService.getWallet(memberId);
        int balance = wallet.calculateCurrentBalance();

        List<EarnedPointSummary> points = wallet.getEarnedPoints().stream()
                .map(EarnedPointSummary::from)
                .toList();

        PointWalletResponse response = new PointWalletResponse(
                wallet.getMemberId(),
                balance,
                points
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 포인트 사용
     * POST /api/point/{memberId}/use
     */
    @PostMapping("/{memberId}/use")
    public ResponseEntity<PointUsageResponse> usePoint(
            @PathVariable Long memberId,
            @Valid @RequestBody UsePointRequest request
    ) {
        PointUsage usage = pointWalletService.usePoint(
                memberId,
                request.amount(),
                request.orderNo()
        );

        PointUsageResponse response = PointUsageResponse.from(usage);
        return ResponseEntity.ok(response);
    }


}

package com.example.pointsystem.presentation.wallet;

import com.example.pointsystem.application.wallet.PointWalletService;
import com.example.pointsystem.domain.wallet.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointWalletController.class)
@AutoConfigureRestDocs(outputDir = "build/generated-snippets")
class PointWalletControllerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointWalletService pointWalletService;

    @Test
    // 의도: 적립 API의 요청 필드/경로 파라미터 문서가 최신 스펙과 일치하는지 검증한다.
    void earnPoint_documents_api() throws Exception {
        doNothing().when(pointWalletService).earnPoint(eq(1L), eq(1000), any(LocalDateTime.class), eq("NORMAL"));

        String requestBody = """
                {
                  "amount": 1000,
                  "expireAt": "2026-12-31T00:00:00",
                  "sourceType": "NORMAL"
                }
                """;

        mockMvc.perform(post("/api/point/{memberId}/earn", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andDo(document("point/earn",
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별자")
                        ),
                        requestFields(
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("적립 금액"),
                                fieldWithPath("expireAt").type(JsonFieldType.STRING).description("만료 일시(ISO-8601), 생략 시 365일"),
                                fieldWithPath("sourceType").type(JsonFieldType.STRING).description("적립 유형(NORMAL/ADMIN/EVENT/COMPENSATION)")
                        )
                ));
    }

    @Test
    // 의도: 지갑 조회 API의 응답 구조를 문서화하여 클라이언트 계약을 고정한다.
    void getWallet_documents_api() throws Exception {
        when(pointWalletService.getWallet(1L)).thenReturn(sampleWallet(1L));

        mockMvc.perform(get("/api/point/{memberId}", 1L))
                .andExpect(status().isOk())
                .andDo(document("point/get-wallet",
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별자")
                        ),
                        responseFields(
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("balance").type(JsonFieldType.NUMBER).description("현재 잔액"),
                                fieldWithPath("points").type(JsonFieldType.ARRAY).description("적립 포인트 목록"),
                                fieldWithPath("points[].earnedPointId").type(JsonFieldType.NUMBER).description("적립 포인트 식별자"),
                                fieldWithPath("points[].amount").type(JsonFieldType.NUMBER).description("적립 금액"),
                                fieldWithPath("points[].remainingAmount").type(JsonFieldType.NUMBER).description("남은 금액"),
                                fieldWithPath("points[].expireAt").type(JsonFieldType.STRING).description("만료 일시"),
                                fieldWithPath("points[].sourceType").type(JsonFieldType.STRING).description("적립 유형"),
                                fieldWithPath("points[].status").type(JsonFieldType.STRING).description("적립 상태"),
                                fieldWithPath("points[].createdAt").type(JsonFieldType.STRING).description("생성 일시")
                        )
                ));
    }

    @Test
    // 의도: 포인트 사용 API의 요청/응답 스키마를 문서로 생성한다.
    void usePoint_documents_api() throws Exception {
        when(pointWalletService.usePoint(1L, 300, "ORDER-1")).thenReturn(sampleUsage(1L, "ORDER-1", 300));

        String requestBody = """
                {
                  "amount": 300,
                  "orderNo": "ORDER-1"
                }
                """;

        mockMvc.perform(post("/api/point/{memberId}/use", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(document("point/use",
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별자")
                        ),
                        requestFields(
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("사용 금액"),
                                fieldWithPath("orderNo").type(JsonFieldType.STRING).description("주문 번호(멱등성 키)")
                        ),
                        responseFields(
                                fieldWithPath("usageId").type(JsonFieldType.NUMBER).description("사용 이력 식별자"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("orderNo").type(JsonFieldType.STRING).description("주문 번호"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("사용 금액"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("사용 일시"),
                                fieldWithPath("details").type(JsonFieldType.ARRAY).description("차감 상세"),
                                fieldWithPath("details[].earnedPointId").type(JsonFieldType.NUMBER).description("차감된 적립 포인트 식별자"),
                                fieldWithPath("details[].amount").type(JsonFieldType.NUMBER).description("차감 금액")
                        )
                ));
    }

    @Test
    // 의도: 적립 취소 API의 경로 파라미터 계약을 문서로 검증한다.
    void cancelEarn_documents_api() throws Exception {
        doNothing().when(pointWalletService).cancelEarn(1L, 10L);

        mockMvc.perform(post("/api/point/{memberId}/earn/{earnedPointId}/cancel", 1L, 10L))
                .andExpect(status().isOk())
                .andDo(document("point/cancel-earn",
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별자"),
                                parameterWithName("earnedPointId").description("적립 포인트 식별자")
                        )
                ));
    }

    @Test
    // 의도: 부분 취소 API의 요청 필드와 응답 필드를 문서화한다.
    void cancelUse_documents_api() throws Exception {
        when(pointWalletService.cancelUse(1L, 20L, 100)).thenReturn(sampleUsage(1L, "ORDER-1", 200));

        String requestBody = """
                {
                  "cancelAmount": 100
                }
                """;

        mockMvc.perform(post("/api/point/{memberId}/use/{usageId}/cancel", 1L, 20L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andDo(document("point/cancel-use",
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별자"),
                                parameterWithName("usageId").description("사용 이력 식별자")
                        ),
                        requestFields(
                                fieldWithPath("cancelAmount").type(JsonFieldType.NUMBER).description("부분 취소 금액")
                        ),
                        responseFields(
                                fieldWithPath("usageId").type(JsonFieldType.NUMBER).description("사용 이력 식별자"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("orderNo").type(JsonFieldType.STRING).description("주문 번호"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("취소 반영 후 사용 금액"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("사용 일시"),
                                fieldWithPath("details").type(JsonFieldType.ARRAY).description("남은 사용 상세"),
                                fieldWithPath("details[].earnedPointId").type(JsonFieldType.NUMBER).description("적립 포인트 식별자"),
                                fieldWithPath("details[].amount").type(JsonFieldType.NUMBER).description("남은 금액")
                        )
                ));
    }

    @Test
    // 의도: 전체 취소 API의 응답 구조가 문서와 동일한지 보장한다.
    void cancelUseAll_documents_api() throws Exception {
        when(pointWalletService.cancelUseAll(1L, 20L)).thenReturn(sampleUsage(1L, "ORDER-1", 0));

        mockMvc.perform(post("/api/point/{memberId}/use/{usageId}/cancel/all", 1L, 20L))
                .andExpect(status().isOk())
                .andDo(document("point/cancel-use-all",
                        pathParameters(
                                parameterWithName("memberId").description("회원 식별자"),
                                parameterWithName("usageId").description("사용 이력 식별자")
                        ),
                        responseFields(
                                fieldWithPath("usageId").type(JsonFieldType.NUMBER).description("사용 이력 식별자"),
                                fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("orderNo").type(JsonFieldType.STRING).description("주문 번호"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("취소 반영 후 사용 금액(0)"),
                                fieldWithPath("createdAt").type(JsonFieldType.STRING).description("사용 일시"),
                                fieldWithPath("details").type(JsonFieldType.ARRAY).description("남은 사용 상세"),
                                fieldWithPath("details[].earnedPointId").type(JsonFieldType.NUMBER).description("적립 포인트 식별자"),
                                fieldWithPath("details[].amount").type(JsonFieldType.NUMBER).description("남은 금액")
                        )
                ));
    }

    private PointWallet sampleWallet(Long memberId) {
        EarnedPoint earnedPoint = new EarnedPoint(
                10L,
                1000,
                700,
                LocalDateTime.of(2026, 12, 31, 0, 0),
                EarnedPointSourceType.NORMAL,
                EarnedPointStatus.ACTIVE,
                LocalDateTime.of(2026, 3, 1, 12, 0)
        );
        return new PointWallet(memberId, new ArrayList<>(List.of(earnedPoint)));
    }

    private PointUsage sampleUsage(Long memberId, String orderNo, int amount) {
        List<PointUsageDetail> details = new ArrayList<>(List.of(
                PointUsageDetail.of(10L, Math.max(amount, 0))
        ));
        return new PointUsage(
                20L,
                memberId,
                orderNo,
                amount,
                details,
                LocalDateTime.of(2026, 3, 1, 12, 30)
        );
    }
}

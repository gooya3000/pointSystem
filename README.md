# 포인트 도메인 DDD 설계 요약

## 1. Aggregate: PointWallet (회원 포인트 계좌)

### 역할
- 회원의 모든 포인트 상태(state)를 관리하는 Aggregate Root
- 적립/사용/취소/만료 등 **현재 상태 변화**를 책임짐

### 필드
- `memberId`
- `earnedPoints: List<EarnedPoint>`

### 메서드
- `earn(amount, sourceType, expireAt)`
- `cancelEarn(earnedPointId)`
- `use(amount, orderNo)`
- `cancelUse(usageId, cancelAmount)`

---

## 2. Entity: EarnedPoint (적립 포인트 단위)

### 역할
- 적립 포인트 하나의 상태를 나타내는 엔티티
- 남은 금액, 만료 여부, 사용 가능 여부 등을 관리

### 필드
- `earnedPointId`
- `amount`
- `remainingAmount`
- `expireAt`
- `sourceType`
- `status`

### 메서드
- `use(amount)`
- `canCancel()`

---

## 3. Entity: PointUsage (주문 단위 사용 정보)

### 역할
- 특정 주문에서 포인트를 사용한 내역을 표현하는 엔티티  
- “현재 기준” 사용 상세(remaining usage detail)를 보유  
- 발생했던 이벤트들을 히스토리로 보존할 수 있음 (optional)

### 필드
- `usageId`
- `orderNo`
- `usedAmount`  (현재 순사용 금액)
- `usedDetails: List<PointUsageDetail>`  (현재 기준 EarnedPoint별 사용 분배)
- `events: List<PointUsageEvent>`  (과거 사용/취소 이벤트 기록)

### 메서드
- `applyUse(amount, details)`
- `applyCancel(cancelAmount, details)`

---

## 4. Value Object: PointUsageDetail (사용 상세)

### 역할
- 특정 사용/취소에서 EarnedPoint별 금액 분배를 나타내는 값 객체
- “불변”으로 다루고, 변경이 필요하면 전체 리스트를 새로 구성해 교체

### 필드
- `earnedPointId`
- `amount`

---

## 5. Value Object: PointUsageEvent (사용 이벤트 기록)

### 역할
- 사용/사용취소가 발생할 때마다 “당시 발생한 사실”을 기록하는 VO
- 상태 변경과 별개로 과거 이력에만 의존 → 불변 값에 가깝다

### 필드
- `eventType` (USE, USE_CANCEL)
- `amount` (이 이벤트에서 변한 금액)
- `details: List<PointUsageDetail>` (이 이벤트 당시 EarnedPoint 분배)
- `createdAt`

---

## 6. 설계 의도 요약

### ✔ 상태(state)와 히스토리(history)의 분리
- **PointWallet + EarnedPoint** → 현재 상태 유지  
- **PointUsageEvent** → 과거 사실을 쌓는 히스토리

### ✔ Aggregate 간 책임 분리
- PointWallet은 “회원 포인트의 정합성 유지”만 담당  
- PointUsage는 “주문 단위 사용 정보 관리”  
- PointUsageEvent는 “발생한 사건의 스냅샷” (불변)

### ✔ 단순하지만 확장 가능한 구조
- 과제 범위에서는 사용/취소 로직 구현이 단순  
- 실무 확장 시 Ledger/Audit 테이블로 자연스럽게 발전 가능

---

## 7. 전체 구조 요약도

```
[Agg: PointWallet]
    └── [Entity: EarnedPoint]

[Entity: PointUsage]
    ├── [Value: PointUsageDetail] (현재 기준)
    └── [Value: PointUsageEvent] (이력)
         └── [Value: PointUsageDetail] (이벤트 당시 기준)
```

---

## 8. 적용 시나리오 예시

### ① 사용
- EarnedPoint에서 우선순위에 따라 금액 차감
- PointUsage.usedDetails 재계산
- PointUsageEvent(eventType=USE) 기록 추가

### ② 사용취소
- EarnedPoint.remainingAmount 복구 (또는 만료 시 신규 Earn 생성)
- PointUsage.usedDetails 재계산
- PointUsageEvent(eventType=USE_CANCEL) 기록 추가

---

## 9. 이 구조의 장점

- 사용/부분취소/취소 모두 표현 가능
- 최초 사용 분배 & 취소 분배 모두 복원 가능
- 현재 상태 & 과거 이력 모두 명확히 분리
- 도메인 규칙이 Aggregate 내부에 잘 모임
- 추적/감사/정산 기능 확장에도 유리

---

## 10. 참고

본 설계는 의도적으로 단순화되어 있지만,  
동일 구조를 실무 포인트/정산 시스템에도 쉽게 확장할 수 있는 형태임.

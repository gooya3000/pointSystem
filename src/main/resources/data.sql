INSERT INTO point_policy (
    max_earn_per_txn,
    max_balance,
    updated_at
) VALUES (
100000,      -- 1회 최대 적립 포인트 (예: 10만 포인트)
1000000,         -- 최대 보유 포인트 (예: 100만 포인트)
CURRENT_TIMESTAMP
);
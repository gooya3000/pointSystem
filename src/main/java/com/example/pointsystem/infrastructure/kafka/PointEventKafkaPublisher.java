package com.example.pointsystem.infrastructure.kafka;

import com.example.pointsystem.application.event.PointChangedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 포인트 변경 이벤트를 트랜잭션 커밋 이후 Kafka로 발행합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventKafkaPublisher {

    private final KafkaTemplate<String, PointChangedEvent> kafkaTemplate;

    @Value("${app.kafka.point-topic:point-events}")
    private String topic;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(PointChangedEvent event) {
        kafkaTemplate.send(topic, String.valueOf(event.memberId()), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.warn("포인트 이벤트 발행 실패: eventId={}, type={}", event.eventId(), event.eventType(), ex);
                    }
                });
    }
}

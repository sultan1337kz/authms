package com.authms.kafka;

import com.authms.model.kafka.MyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    public static final String TOPIC = "DEMO_KAFKA";

    private final KafkaTemplate<String, MyEvent> kafkaTemplate;

    public void sendFlightEvent(MyEvent event) {
        String key = event.getKey();
        kafkaTemplate.send(TOPIC, key, event);
        log.info("Producer produced the message {}", event);
        // Add handlers or post-processing logic if needed
    }
}

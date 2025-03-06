package com.authms.kafka;

import com.authms.model.kafka.MyEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "DEMO_KAFKA", groupId = "123")
    public void flightEventConsumer(MyEvent message) {
        log.info("Consumer consumed Kafka message -> {}", message);

        // Add your handlers and post-processing logic here
    }

}

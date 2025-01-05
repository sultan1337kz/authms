package com.authms.controller;

import com.authms.kafka.KafkaProducer;
import com.authms.model.MyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaController {

    private final KafkaProducer kafkaProducer;

    @PostMapping("/publish")
    public ResponseEntity<String> publishMessage(@RequestBody MyEvent event) {
        kafkaProducer.sendFlightEvent(event);
        return ResponseEntity.ok("Message published successfully: " + event);
    }
}
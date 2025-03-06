package com.authms.model.kafka;

import lombok.Data;

@Data
public class MyEvent {
    private String key;
    private String value;
}
package com.authms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.authms"})
public class SecureAuthApplication {
    public static void main(String[] args) {
        SpringApplication.run(SecureAuthApplication.class, args);
    }
}

package com.example.intent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.example.intent.model")
@EnableJpaRepositories("com.example.intent.repository")
public class IntentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntentServiceApplication.class, args);
    }
} 
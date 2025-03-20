package com.example.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import com.example.common.config.RestClientConfig;
import com.example.common.config.DatabaseConfig;

@SpringBootApplication
@EntityScan({"com.example.chat.model", "com.example.common.model"})
@EnableJpaRepositories("com.example.chat.repository")
@ComponentScan({"com.example.chat", "com.example.common.client"})
@Import({RestClientConfig.class, DatabaseConfig.class})
public class ChatServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatServiceApplication.class, args);
    }
} 
package com.example.conversation.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sessionId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String role; // user 或 assistant

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private String markdownPath; // Markdown 文件路径

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
} 
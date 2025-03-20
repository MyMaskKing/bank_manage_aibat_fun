package com.example.common.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Integer recordId;
    
    @Column(name = "user_input", nullable = false, columnDefinition = "TEXT")
    private String userInput;
    
    @Column(name = "api_result", nullable = false, columnDefinition = "TEXT")
    private String apiResult;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp = LocalDateTime.now();
} 
package com.example.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String content;
    private boolean success = true;
    private String errorMessage;
    
    public ChatResponse(String content) {
        this.content = content;
    }
    
    public ChatResponse(boolean success, String errorMessage) {
        this.success = success;
        this.errorMessage = errorMessage;
    }
} 
package com.example.chat.controller;

import com.example.chat.service.ChatService;
import com.example.common.model.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/api/chat")
    public ResponseEntity<Map<String, String>> processUserInput(@RequestBody Map<String, String> request) {
        String userInput = request.get("message");
        log.info("收到用户输入: {}", userInput);
        
        try {
            ChatResponse response = chatService.processUserInput(userInput);
            Map<String, String> result = new HashMap<>();
            result.put("response", response.getContent());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("处理用户输入时发生错误", e);
            Map<String, String> result = new HashMap<>();
            result.put("response", "抱歉，处理您的请求时发生错误：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    
    @PostMapping("/api/upload")
    public ResponseEntity<Map<String, String>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        log.info("收到文件上传: {}, 大小: {}", file.getOriginalFilename(), file.getSize());
        
        Map<String, String> result = new HashMap<>();
        
        try {
            // 处理Excel或CSV文件
            String processResult = chatService.processUploadedFile(file);
            result.put("message", processResult);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("处理上传文件时发生错误", e);
            result.put("message", "文件处理失败: " + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/api/conversations")
    public ResponseEntity<List<Map<String, String>>> getAllConversations() {
        return ResponseEntity.ok(chatService.getAllConversations());
    }
} 
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
    public ResponseEntity<Map<String, String>> processUserInput(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "message", required = false) String message) {
        log.info("收到用户输入: {}, 文件: {}", message, file != null ? file.getOriginalFilename() : "无");
        
        try {
            String response;
            if (file != null && !file.isEmpty()) {
                // 如果有文件，使用文件处理逻辑
                response = chatService.processUploadedFile(file, message);
            } else {
                // 如果没有文件，使用普通聊天处理逻辑
                ChatResponse chatResponse = chatService.processUserInput(message);
                response = chatResponse.getContent();
            }
            
            Map<String, String> result = new HashMap<>();
            result.put("response", response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("处理用户输入时发生错误", e);
            Map<String, String> result = new HashMap<>();
            result.put("response", "抱歉，处理您的请求时发生错误：" + e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    @GetMapping("/api/conversations")
    public ResponseEntity<List<Map<String, String>>> getAllConversations() {
        return ResponseEntity.ok(chatService.getAllConversations());
    }
} 
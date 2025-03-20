package com.example.conversation.controller;

import com.example.conversation.service.ConversationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    
    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveConversation(@RequestBody Map<String, String> conversation) {
        String userInput = conversation.get("userInput");
        String apiResult = conversation.get("apiResult");
        
        log.info("保存对话记录: userInput={}, apiResult={}", userInput, apiResult);
        
        boolean saved = conversationService.saveConversation(userInput, apiResult);
        
        if (saved) {
            return ResponseEntity.ok(Map.of("status", "success", "message", "对话记录保存成功"));
        } else {
            return ResponseEntity.ok(Map.of("status", "error", "message", "对话记录保存失败"));
        }
    }
} 
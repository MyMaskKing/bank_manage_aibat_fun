package com.example.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 意图识别测试控制器
 */
@Controller
public class TestIntentController {

    private final RestTemplate restTemplate;
    
    public TestIntentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * 返回测试页面
     */
    @GetMapping("/test-intent")
    public String testIntentPage() {
        return "test-intent";
    }
    
    /**
     * 转发到意图识别服务的完整解析API
     */
    @PostMapping("/intent/parse/full")
    @ResponseBody
    public Map<String, Object> parseIntent(@RequestBody Map<String, String> request) {
        return restTemplate.postForObject(
            "http://localhost:8081/intent/parse/full",
            request,
            Map.class
        );
    }
} 
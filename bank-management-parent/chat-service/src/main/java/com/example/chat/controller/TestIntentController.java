package com.example.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.logging.Logger;

/**
 * 意图识别测试控制器
 */
@Controller
public class TestIntentController {
    
    private static final Logger logger = Logger.getLogger(TestIntentController.class.getName());
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
        logger.info("发送意图解析请求: " + request);
        Map<String, Object> response = restTemplate.postForObject(
            "http://localhost:8081/intent/parse/full",
            request,
            Map.class
        );
        
        // 日志记录响应，帮助调试
        logger.info("收到意图解析响应: " + response);
        
        return response;
    }
} 
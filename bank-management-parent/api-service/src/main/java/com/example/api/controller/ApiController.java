package com.example.api.controller;

import com.example.api.service.ApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final ApiService apiService;
    
    @PostMapping("/execute")
    public String executeApi(@RequestBody Map<String, Object> request) {
        log.info("收到API执行请求: {}", request);
        
        String apiName = extractApiName(request.get("apiName"));
        String userInput = (String) request.get("userInput");
        
        log.info("解析后的API名称: {}, 用户输入: {}", apiName, userInput);
        
        try {
            return apiService.executeApi(apiName, userInput);
        } catch (Exception e) {
            log.error("执行API失败: {}", apiName, e);
            return "执行API时出错: " + e.getMessage();
        }
    }
    
    /**
     * 从请求中提取API名称，支持字符串和JSON格式
     */
    private String extractApiName(Object apiNameObj) {
        if (apiNameObj == null) {
            return "unknown";
        }
        
        if (apiNameObj instanceof String) {
            String apiName = (String) apiNameObj;
            
            // 检查是否是JSON字符串
            if (apiName.startsWith("{") && apiName.contains("apiName")) {
                // 简单解析，更复杂场景应使用Jackson
                int start = apiName.indexOf("\"apiName\":");
                if (start >= 0) {
                    start += "\"apiName\":".length();
                    int end = apiName.indexOf("\"", start + 1);
                    if (end >= 0) {
                        return apiName.substring(start + 1, end);
                    }
                }
            }
            
            return apiName;
        }
        
        if (apiNameObj instanceof Map) {
            Map<?, ?> apiNameMap = (Map<?, ?>) apiNameObj;
            Object name = apiNameMap.get("apiName");
            if (name != null) {
                return name.toString();
            }
        }
        
        return apiNameObj.toString();
    }
} 
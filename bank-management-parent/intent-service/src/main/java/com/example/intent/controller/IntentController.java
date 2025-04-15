package com.example.intent.controller;

import com.example.intent.model.EventLibraryType;
import com.example.intent.model.IntentRecognitionResult;
import com.example.intent.service.EventLibraryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/intent")
public class IntentController {
    
    @Autowired
    private EventLibraryService eventLibraryService;
    
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 原始意图分析API（保持兼容）
     */
    @PostMapping("/analyze")
    public Map<String, Object> analyzeIntent(@RequestBody Map<String, String> request) {
        String userInput = request.get("userInput");
        // 使用第一次解析结果作为向后兼容的返回
        IntentRecognitionResult result = eventLibraryService.firstParse(userInput);
        Map<String, Object> response = new HashMap<>();
        response.put("apiName", result.getIntentName());
        response.put("callCount", result.getCallCount());
        response.put("params", result.getParameters());
        return response;
    }
    
    /**
     * 第一次解析：分析用户输入，在个人事件库和银行事件库中寻找匹配
     */
    @PostMapping("/parse/first")
    public Map<String, Object> firstParse(@RequestBody Map<String, String> request) {
        String userInput = request.get("userInput");
        IntentRecognitionResult result = eventLibraryService.firstParse(userInput);
        return objectMapper.convertValue(result, Map.class);
    }
    
    /**
     * 第二次解析：根据第一次解析结果，解析关联的银行事件
     */
    @PostMapping("/parse/second")
    public Map<String, Object> secondParse(@RequestBody Map<String, Object> request) {
        IntentRecognitionResult firstResult = objectMapper.convertValue(
            request.get("firstResult"), 
            IntentRecognitionResult.class
        );
        String userInput = (String) request.get("userInput");
        IntentRecognitionResult result = eventLibraryService.secondParse(firstResult, userInput);
        return objectMapper.convertValue(result, Map.class);
    }
    
    /**
     * 第三次解析：根据第二次解析结果，解析关联的标准事件
     */
    @PostMapping("/parse/third")
    public Map<String, Object> thirdParse(@RequestBody Map<String, Object> request) {
        IntentRecognitionResult secondResult = objectMapper.convertValue(
            request.get("secondResult"), 
            IntentRecognitionResult.class
        );
        IntentRecognitionResult result = eventLibraryService.thirdParse(secondResult);
        return objectMapper.convertValue(result, Map.class);
    }
    
    /**
     * 完整三次解析流程
     */
    @PostMapping("/parse/full")
    public Map<String, Object> fullParse(@RequestBody Map<String, Object> request) {
        String userInput = (String) request.get("userInput");
        
        // 第一次解析
        IntentRecognitionResult firstResult = eventLibraryService.firstParse(userInput);
        
        // 第二次解析
        IntentRecognitionResult secondResult = eventLibraryService.secondParse(firstResult, userInput);
        
        // 第三次解析
        IntentRecognitionResult thirdResult = eventLibraryService.thirdParse(secondResult);
        
        // 构建返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("firstResult", firstResult);
        response.put("secondResult", secondResult);
        response.put("thirdResult", thirdResult);
        
        return response;
    }
    
    /**
     * 从Map中解析IntentRecognitionResult
     */
    private IntentRecognitionResult parseResultFromMap(Map<String, Object> map) {
        IntentRecognitionResult result = new IntentRecognitionResult();
        
        result.setIntentName((String) map.get("intentName"));
        result.setLibraryType(EventLibraryType.valueOf((String) map.get("libraryType")));
        result.setCallCount((Integer) map.get("callCount"));
        result.setParameters((Map<String, Object>) map.get("parameters"));
        result.setConfidence((Integer) map.get("confidence"));
        result.setDescription((String) map.get("description"));
        result.setRelatedEvents((java.util.List<String>) map.get("relatedEvents"));
        
        return result;
    }
} 
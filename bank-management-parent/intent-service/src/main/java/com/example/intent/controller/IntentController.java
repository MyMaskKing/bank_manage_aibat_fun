package com.example.intent.controller;

import com.example.intent.model.EventLibraryConfig;
import com.example.intent.model.EventLibraryType;
import com.example.intent.model.IntentRecognitionResult;
import com.example.intent.service.EventLibraryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        
        // 构建返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("firstResult", firstResult);
        
        // 第二次解析
        IntentRecognitionResult secondResult = eventLibraryService.secondParse(firstResult, userInput);
        
        // 如果是个人事件，并且关联了多个银行事件
        if (firstResult.getLibraryType() == EventLibraryType.PERSONAL && 
            firstResult.getRelatedEvents() != null && 
            firstResult.getRelatedEvents().size() > 1) {
            
            // 为每个银行事件创建完整的事件详情
            List<Map<String, Object>> bankEventsList = new ArrayList<>();
            
            // 处理第一个银行事件（已经由 secondParse 处理）
            bankEventsList.add(objectMapper.convertValue(secondResult, Map.class));
            
            // 处理其他银行事件
            for (int i = 1; i < firstResult.getRelatedEvents().size(); i++) {
                String bankEventName = firstResult.getRelatedEvents().get(i);
                
                // 创建模拟的银行事件
                IntentRecognitionResult otherBankEvent = createBankEventResult(
                    bankEventName, 
                    firstResult.getParameters(), 
                    firstResult.getCallCount(), 
                    firstResult.getConfidence(),
                    userInput
                );
                
                bankEventsList.add(objectMapper.convertValue(otherBankEvent, Map.class));
            }
            
            // 返回银行事件列表
            response.put("secondResult", bankEventsList);
        } else {
            // 单个银行事件，直接返回
            response.put("secondResult", secondResult);
        }
        
        // 第三次解析
        IntentRecognitionResult thirdResult = null;
        
        // 如果第二次解析结果是列表，需要合并所有银行事件的标准事件
        if (response.get("secondResult") instanceof List) {
            List<Map<String, Object>> bankEvents = (List<Map<String, Object>>) response.get("secondResult");
            if (!bankEvents.isEmpty()) {
                // 使用第一个银行事件作为基础
                IntentRecognitionResult baseResult = objectMapper.convertValue(
                    bankEvents.get(0), 
                    IntentRecognitionResult.class
                );
                thirdResult = eventLibraryService.thirdParse(baseResult);
                
                // 使用LinkedHashSet收集所有标准事件，保持插入顺序
                LinkedHashSet<String> allStandardEvents = new LinkedHashSet<>(thirdResult.getRelatedEvents());
                
                // 添加其他银行事件的标准事件
                for (int i = 1; i < bankEvents.size(); i++) {
                    IntentRecognitionResult otherBankEvent = objectMapper.convertValue(
                        bankEvents.get(i), 
                        IntentRecognitionResult.class
                    );
                    
                    // 获取此银行事件的标准事件
                    EventLibraryConfig bankConfig = eventLibraryService.getBankEventConfig(otherBankEvent.getIntentName());
                    if (bankConfig != null && bankConfig.getRelatedEventsList() != null) {
                        // 按顺序添加标准事件
                        allStandardEvents.addAll(bankConfig.getRelatedEventsList());
                    }
                }
                
                // 更新最终结果中的相关标准事件，保持顺序
                thirdResult.setRelatedEvents(new ArrayList<>(allStandardEvents));
            } else {
                thirdResult = eventLibraryService.createUnknownResult("无银行事件信息");
            }
        } else {
            // 单个银行事件的标准解析
            thirdResult = eventLibraryService.thirdParse(secondResult);
        }
        
        response.put("thirdResult", thirdResult);
        
        return response;
    }
    
    /**
     * 创建银行事件结果（用于模拟其他银行事件的信息）
     */
    private IntentRecognitionResult createBankEventResult(
            String bankEventName, 
            Map<String, Object> parameters, 
            int callCount, 
            int confidence,
            String userInput) {
        
        // 获取银行事件配置
        EventLibraryConfig bankConfig = eventLibraryService.getBankEventConfig(bankEventName);
        if (bankConfig == null) {
            return eventLibraryService.createUnknownResult("未找到银行事件配置: " + bankEventName);
        }
        
        // 创建新的银行事件结果
        IntentRecognitionResult result = new IntentRecognitionResult();
        
        // 设置基本信息
        result.setIntentName(bankEventName);
        result.setLibraryType(EventLibraryType.BANK);
        result.setDescription(bankConfig.getDescription());
        result.setRelatedEvents(bankConfig.getRelatedEventsList());
        result.setConfidence(confidence);
        result.setCallCount(callCount);
        
        // 复制参数
        result.setParameters(new HashMap<>(parameters));
        
        // 返回结果
        return result;
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
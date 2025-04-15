package com.example.intent.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 意图识别结果模型
 */
public class IntentRecognitionResult {
    /**
     * 识别到的意图名称
     */
    private String intentName;
    
    /**
     * 意图所属的事件库类型
     */
    private EventLibraryType libraryType;
    
    /**
     * 关联的下级事件名称列表
     * - 如果是个人事件，则关联银行事件
     * - 如果是银行事件，则关联标志事件
     */
    private List<String> relatedEvents = new ArrayList<>();
    
    /**
     * 调用次数
     */
    private int callCount = 1;
    
    /**
     * 调用参数
     */
    private Map<String, Object> parameters = new HashMap<>();
    
    /**
     * 置信度分数 (0-100)
     */
    private int confidence = 0;
    
    /**
     * 描述信息
     */
    private String description;
    
    // Getters and Setters
    
    public String getIntentName() {
        return intentName;
    }
    
    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }
    
    public EventLibraryType getLibraryType() {
        return libraryType;
    }
    
    public void setLibraryType(EventLibraryType libraryType) {
        this.libraryType = libraryType;
    }
    
    public List<String> getRelatedEvents() {
        return relatedEvents;
    }
    
    public void setRelatedEvents(List<String> relatedEvents) {
        this.relatedEvents = relatedEvents;
    }
    
    public void addRelatedEvent(String eventName) {
        this.relatedEvents.add(eventName);
    }
    
    public int getCallCount() {
        return callCount;
    }
    
    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }
    
    public Map<String, Object> getParameters() {
        return parameters;
    }
    
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
    
    public void addParameter(String key, Object value) {
        this.parameters.put(key, value);
    }
    
    public int getConfidence() {
        return confidence;
    }
    
    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * 将结果转换为API响应Map
     */
    public Map<String, Object> toResponseMap() {
        Map<String, Object> response = new HashMap<>();
        response.put("intentName", intentName);
        response.put("libraryType", libraryType.name());
        response.put("relatedEvents", relatedEvents);
        response.put("callCount", callCount);
        response.put("parameters", parameters);
        response.put("confidence", confidence);
        response.put("description", description);
        return response;
    }
} 
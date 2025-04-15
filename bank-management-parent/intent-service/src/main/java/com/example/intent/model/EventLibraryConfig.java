package com.example.intent.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 事件库配置模型
 */
public class EventLibraryConfig {
    /**
     * 事件名称
     */
    private String eventName;
    
    /**
     * 事件库类型
     */
    private EventLibraryType libraryType;
    
    /**
     * 下级关联事件名称
     * 可能是单个事件名，也可能是事件名称列表
     */
    private Object relatedEvents;
    
    /**
     * 描述信息
     */
    private String description;
    
    /**
     * 模式匹配正则表达式列表
     */
    private List<Pattern> patterns = new ArrayList<>();
    
    /**
     * 参数提取配置
     * 参数名 -> 参数提取正则表达式
     */
    private Map<String, Pattern> parameterPatterns = new HashMap<>();
    
    /**
     * 是否已实现
     */
    private boolean implemented = false;
    
    /**
     * 示例输入
     */
    private List<String> examples = new ArrayList<>();

    // Getters and Setters
    
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public EventLibraryType getLibraryType() {
        return libraryType;
    }

    public void setLibraryType(EventLibraryType libraryType) {
        this.libraryType = libraryType;
    }

    public Object getRelatedEvents() {
        return relatedEvents;
    }
    
    public List<String> getRelatedEventsList() {
        if (relatedEvents instanceof List) {
            return (List<String>) relatedEvents;
        } else if (relatedEvents instanceof String) {
            List<String> result = new ArrayList<>();
            result.add((String) relatedEvents);
            return result;
        }
        return new ArrayList<>();
    }

    public void setRelatedEvents(Object relatedEvents) {
        this.relatedEvents = relatedEvents;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Pattern> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<Pattern> patterns) {
        this.patterns = patterns;
    }
    
    public void addPattern(Pattern pattern) {
        this.patterns.add(pattern);
    }

    public Map<String, Pattern> getParameterPatterns() {
        return parameterPatterns;
    }

    public void setParameterPatterns(Map<String, Pattern> parameterPatterns) {
        this.parameterPatterns = parameterPatterns;
    }
    
    public void addParameterPattern(String paramName, Pattern pattern) {
        this.parameterPatterns.put(paramName, pattern);
    }

    public boolean isImplemented() {
        return implemented;
    }

    public void setImplemented(boolean implemented) {
        this.implemented = implemented;
    }

    public List<String> getExamples() {
        return examples;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }
    
    public void addExample(String example) {
        this.examples.add(example);
    }
} 
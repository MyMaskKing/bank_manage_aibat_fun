package com.example.intent.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Slf4j
@Service
public class IntentService {
    
    // 意图匹配规则定义
    private final Map<String, List<Pattern>> intentPatterns = new HashMap<>();
    
    // 意图到API名称的映射
    private final Map<String, String> intentToApiMap = new HashMap<>();
    
    // 意图实现状态
    private final Map<String, Boolean> intentImplementationStatus = new HashMap<>();
    
    // 常见中文名字
    private final String[] commonNames = {"张三", "李四", "王五", "赵六", "陈七", "刘八", "杨九", "周十"};
    
    @PostConstruct
    public void init() {
        loadIntentPatternsFromFile();
    }
    
    private void loadIntentPatternsFromFile() {
        try {
            ClassPathResource resource = new ClassPathResource("intents/intent_patterns.md");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            
            String line;
            String currentIntent = null;
            List<String> currentPatterns = new ArrayList<>();
            boolean isInPatternsBlock = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // 跳过空行和普通注释
                if (line.isEmpty() || (line.startsWith("#") && !line.startsWith("###"))) {
                    continue;
                }
                
                // 检测意图定义
                if (line.startsWith("### ")) {
                    // 如果有之前的意图，先保存
                    if (currentIntent != null && !currentPatterns.isEmpty()) {
                        addIntent(currentIntent, currentPatterns);
                    }
                    
                    // 开始新的意图
                    currentIntent = line.substring(4).trim();
                    currentPatterns = new ArrayList<>();
                    isInPatternsBlock = false;
                    continue;
                }
                
                // 检测API名称
                if (line.startsWith("- API_NAME: ")) {
                    String apiName = line.substring("- API_NAME: ".length()).trim();
                    if (currentIntent != null) {
                        intentToApiMap.put(currentIntent, apiName);
                    }
                    continue;
                }
                
                // 检测实现状态
                if (line.startsWith("- Status: ")) {
                    String status = line.substring("- Status: ".length()).trim();
                    if (currentIntent != null) {
                        intentImplementationStatus.put(currentIntent, "IMPLEMENTED".equals(status));
                    }
                    continue;
                }
                
                // 检测模式块的开始和结束
                if (line.equals("```")) {
                    isInPatternsBlock = !isInPatternsBlock;
                    continue;
                }
                
                // 如果在模式块内，收集模式
                if (isInPatternsBlock && !line.isEmpty()) {
                    currentPatterns.add(line);
                }
            }
            
            // 保存最后一个意图
            if (currentIntent != null && !currentPatterns.isEmpty()) {
                addIntent(currentIntent, currentPatterns);
            }
            
            log.info("成功加载 {} 个意图配置", intentPatterns.size());
            intentPatterns.keySet().forEach(intent -> 
                log.info("意图: {}, API: {}, 已实现: {}", 
                    intent, 
                    intentToApiMap.get(intent),
                    intentImplementationStatus.get(intent))
            );
            
        } catch (Exception e) {
            log.error("加载意图配置文件失败", e);
            throw new RuntimeException("加载意图配置文件失败: " + e.getMessage(), e);
        }
    }
    
    private void addIntent(String intent, List<String> patterns) {
        List<Pattern> compiledPatterns = patterns.stream()
            .map(Pattern::compile)
            .toList();
        intentPatterns.put(intent, compiledPatterns);
    }
    
    public String analyzeIntent(String userInput) {
        if (!StringUtils.hasText(userInput)) {
            return "UNKNOWN";
        }
        
        // 提取所有客户名
        List<String> customerNames = extractCustomerNames(userInput);
        
        for (Map.Entry<String, List<Pattern>> entry : intentPatterns.entrySet()) {
            String intent = entry.getKey();
            List<Pattern> patterns = entry.getValue();
            
            for (Pattern pattern : patterns) {
                Matcher matcher = pattern.matcher(userInput);
                if (matcher.matches()) {
                    String apiName = intentToApiMap.get(intent);
                    
                    // 如果是测试中的API，添加提示信息
                    if (!intentImplementationStatus.get(intent)) {
                        log.warn("用户请求了测试中的API: {}", apiName);
                        apiName = "test_" + apiName;
                    }
                    
                    // 如果找到多个客户名，将它们作为参数添加到API名称中
                    if (customerNames.size() > 1) {
                        apiName += "?customers=" + String.join(",", customerNames);
                    }
                    
                    return apiName;
                }
            }
        }
        
        return "UNKNOWN";
    }
    
    /**
     * 从用户输入中提取客户名称列表
     */
    private List<String> extractCustomerNames(String userInput) {
        List<String> names = new ArrayList<>();
        
        // 检查每个常见名字是否在输入中出现
        for (String name : commonNames) {
            if (userInput.contains(name)) {
                names.add(name);
            }
        }
        
        return names;
    }
} 
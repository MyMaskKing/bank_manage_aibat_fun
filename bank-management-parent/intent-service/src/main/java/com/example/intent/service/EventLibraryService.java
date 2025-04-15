package com.example.intent.service;

import com.example.intent.model.EventLibraryConfig;
import com.example.intent.model.EventLibraryType;
import com.example.intent.model.IntentRecognitionResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 事件库服务
 * 负责加载和管理多层事件库配置
 */
@Slf4j
@Service
public class EventLibraryService {

    // 个人事件库
    private final Map<String, EventLibraryConfig> personalEventLibrary = new HashMap<>();
    
    // 银行事件库
    private final Map<String, EventLibraryConfig> bankEventLibrary = new HashMap<>();
    
    // 标准事件库
    private final Map<String, EventLibraryConfig> standardEventLibrary = new HashMap<>();
    
    // 常见中文名字
    private final String[] commonNames = {"张三", "李四", "王五", "赵六", "陈七", "刘八", "杨九", "周十"};
    
    // 值匹配正则表达式
    private final Map<String, Pattern> commonValuePatterns = new HashMap<>();
    
    @PostConstruct
    public void init() {
        // 初始化常用值匹配模式
        commonValuePatterns.put("otpValue", Pattern.compile(".*(开启|打开|启用|激活|true|关闭|禁用|停用|false).*"));
        
        // 加载三种事件库配置
        loadEventLibrary("intents/personal_event_patterns.md", personalEventLibrary, EventLibraryType.PERSONAL);
        loadEventLibrary("intents/bank_event_patterns.md", bankEventLibrary, EventLibraryType.BANK);
        loadEventLibrary("intents/standard_event_patterns.md", standardEventLibrary, EventLibraryType.STANDARD);
        
        // 打印加载统计
        log.info("成功加载个人事件库: {} 个配置", personalEventLibrary.size());
        log.info("成功加载银行事件库: {} 个配置", bankEventLibrary.size());
        log.info("成功加载标准事件库: {} 个配置", standardEventLibrary.size());
    }
    
    /**
     * 加载事件库配置文件
     */
    private void loadEventLibrary(String resourcePath, Map<String, EventLibraryConfig> libraryMap, 
                                 EventLibraryType libraryType) {
        try {
            ClassPathResource resource = new ClassPathResource(resourcePath);
            if (!resource.exists()) {
                log.warn("事件库配置文件不存在: {}", resourcePath);
                return;
            }
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            
            String line;
            EventLibraryConfig currentConfig = null;
            List<String> currentPatterns = new ArrayList<>();
            Map<String, List<String>> currentParamPatterns = new HashMap<>();
            String currentParamName = null;
            boolean isInPatternsBlock = false;
            boolean isInParamPatternsBlock = false;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // 跳过空行和普通注释
                if (line.isEmpty() || (line.startsWith("#") && !line.startsWith("###"))) {
                    continue;
                }
                
                // 检测事件定义
                if (line.startsWith("### ")) {
                    // 如果有之前的配置，先保存
                    if (currentConfig != null && !currentPatterns.isEmpty()) {
                        saveEventConfig(currentConfig, currentPatterns, currentParamPatterns, libraryMap);
                    }
                    
                    // 开始新的配置
                    currentConfig = new EventLibraryConfig();
                    currentConfig.setEventName(line.substring(4).trim());
                    currentConfig.setLibraryType(libraryType);
                    currentPatterns = new ArrayList<>();
                    currentParamPatterns = new HashMap<>();
                    isInPatternsBlock = false;
                    isInParamPatternsBlock = false;
                    continue;
                }
                
                // 检测关联事件
                if (currentConfig != null) {
                    if (line.startsWith("- BANK_EVENT: ")) {
                        String relatedEventText = line.substring("- BANK_EVENT: ".length()).trim();
                        if (relatedEventText.startsWith("[") && relatedEventText.endsWith("]")) {
                            // 多个银行事件
                            String[] events = relatedEventText.substring(1, relatedEventText.length() - 1).split(",\\s*");
                            currentConfig.setRelatedEvents(Arrays.asList(events));
                        } else {
                            // 单个银行事件
                            currentConfig.setRelatedEvents(relatedEventText);
                        }
                        continue;
                    } else if (line.startsWith("- STANDARD_EVENT: ")) {
                        String relatedEventText = line.substring("- STANDARD_EVENT: ".length()).trim();
                        if (relatedEventText.startsWith("[") && relatedEventText.endsWith("]")) {
                            // 多个标准事件
                            String[] events = relatedEventText.substring(1, relatedEventText.length() - 1).split(",\\s*");
                            currentConfig.setRelatedEvents(Arrays.asList(events));
                        } else {
                            // 单个标准事件
                            currentConfig.setRelatedEvents(relatedEventText);
                        }
                        continue;
                    } else if (line.startsWith("- API_NAME: ")) {
                        String apiName = line.substring("- API_NAME: ".length()).trim();
                        currentConfig.setRelatedEvents(apiName);
                        continue;
                    }
                    
                    // 检测描述
                    if (line.startsWith("- Description: ")) {
                        String description = line.substring("- Description: ".length()).trim();
                        currentConfig.setDescription(description);
                        continue;
                    }
                    
                    // 检测实现状态
                    if (line.startsWith("- Status: ")) {
                        String status = line.substring("- Status: ".length()).trim();
                        currentConfig.setImplemented("IMPLEMENTED".equals(status));
                        continue;
                    }
                    
                    // 检测参数提取部分
                    if (line.startsWith("- ParameterExtraction:")) {
                        continue; // 跳过标题行
                    }
                    
                    if (line.startsWith("  - ") && !isInPatternsBlock && !isInParamPatternsBlock) {
                        currentParamName = line.substring(4).split(":")[0].trim();
                        currentParamPatterns.putIfAbsent(currentParamName, new ArrayList<>());
                        continue;
                    }
                    
                    // 检测模式块的开始和结束
                    if (line.equals("```")) {
                        if (isInParamPatternsBlock) {
                            isInParamPatternsBlock = false;
                            currentParamName = null;
                        } else if (isInPatternsBlock) {
                            isInPatternsBlock = false;
                        } else if (currentParamName != null) {
                            isInParamPatternsBlock = true;
                        } else {
                            isInPatternsBlock = true;
                        }
                        continue;
                    }
                    
                    // 如果在模式块内，收集模式
                    if (isInPatternsBlock && !line.isEmpty()) {
                        currentPatterns.add(line);
                    } else if (isInParamPatternsBlock && !line.isEmpty() && currentParamName != null) {
                        currentParamPatterns.get(currentParamName).add(line);
                    }
                    
                    // 收集示例
                    if (!isInPatternsBlock && !isInParamPatternsBlock && line.startsWith("  - ")) {
                        String example = line.substring(4).trim();
                        currentConfig.addExample(example);
                    }
                }
            }
            
            // 保存最后一个配置
            if (currentConfig != null && !currentPatterns.isEmpty()) {
                saveEventConfig(currentConfig, currentPatterns, currentParamPatterns, libraryMap);
            }
            
        } catch (Exception e) {
            log.error("加载事件库配置文件失败: {}", resourcePath, e);
            throw new RuntimeException("加载事件库配置文件失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 保存事件配置
     */
    private void saveEventConfig(EventLibraryConfig config, List<String> patterns, 
                               Map<String, List<String>> paramPatterns, 
                               Map<String, EventLibraryConfig> libraryMap) {
        // 编译主模式正则表达式
        for (String pattern : patterns) {
            try {
                config.addPattern(Pattern.compile(pattern));
            } catch (Exception e) {
                log.error("编译正则表达式失败: {} - {}", pattern, e.getMessage());
            }
        }
        
        // 编译参数提取正则表达式
        for (Map.Entry<String, List<String>> entry : paramPatterns.entrySet()) {
            String paramName = entry.getKey();
            for (String paramPattern : entry.getValue()) {
                try {
                    config.addParameterPattern(paramName, Pattern.compile(paramPattern));
                } catch (Exception e) {
                    log.error("编译参数正则表达式失败: {} - {} - {}", paramName, paramPattern, e.getMessage());
                }
            }
        }
        
        // 保存到事件库
        libraryMap.put(config.getEventName(), config);
    }
    
    /**
     * 第一次解析：分析输入，匹配个人事件库或银行事件库
     */
    public IntentRecognitionResult firstParse(String userInput) {
        if (!StringUtils.hasText(userInput)) {
            return createUnknownResult("输入为空");
        }
        
        // 首先尝试匹配个人事件库
        IntentRecognitionResult personalResult = matchEventLibrary(userInput, personalEventLibrary, EventLibraryType.PERSONAL);
        if (personalResult.getConfidence() > 0) {
            return personalResult;
        }
        
        // 然后尝试匹配银行事件库
        IntentRecognitionResult bankResult = matchEventLibrary(userInput, bankEventLibrary, EventLibraryType.BANK);
        if (bankResult.getConfidence() > 0) {
            return bankResult;
        }
        
        // 都没有匹配到，返回未知结果
        return createUnknownResult("未匹配到任何意图");
    }
    
    /**
     * 第二次解析：根据第一次解析结果，解析关联的银行事件
     */
    public IntentRecognitionResult secondParse(IntentRecognitionResult firstResult, String userInput) {
        if (firstResult == null) {
            return createUnknownResult("第一次解析结果为空");
        }

        if (firstResult.getLibraryType() == EventLibraryType.BANK) {
            // 如果第一次识别结果就是银行事件，直接返回
            return firstResult;
        } else if (firstResult.getLibraryType() == EventLibraryType.PERSONAL) {
            // 如果是个人事件，查找对应的银行事件
            List<String> relatedBankEvents = firstResult.getRelatedEvents();
            if (relatedBankEvents.isEmpty()) {
                return createUnknownResult("个人事件未关联银行事件");
            }
            
            // 获取第一个关联的银行事件配置
            String bankEventName = relatedBankEvents.get(0);
            EventLibraryConfig bankConfig = bankEventLibrary.get(bankEventName);
            if (bankConfig == null) {
                return createUnknownResult("未找到关联的银行事件配置");
            }
            
            // 创建银行事件结果
            IntentRecognitionResult bankResult = new IntentRecognitionResult();
            
            // 设置银行事件基本信息
            bankResult.setLibraryType(EventLibraryType.BANK);
            bankResult.setIntentName(bankEventName);
            bankResult.setDescription(bankConfig.getDescription());
            bankResult.setRelatedEvents(relatedBankEvents);
            bankResult.setConfidence(firstResult.getConfidence());
            
            // 合并参数：
            // 1. 首先使用第一次解析的参数
            Map<String, Object> mergedParams = new HashMap<>(firstResult.getParameters());
            
            // 2. 尝试从用户输入中提取银行事件特有的参数
            Map<String, Pattern> bankParamPatterns = bankConfig.getParameterPatterns();
            if (bankParamPatterns != null && !bankParamPatterns.isEmpty()) {
                for (Map.Entry<String, Pattern> entry : bankParamPatterns.entrySet()) {
                    String paramName = entry.getKey();
                    Pattern pattern = entry.getValue();
                    // 如果参数还没有值，尝试从用户输入中提取
                    if (!mergedParams.containsKey(paramName)) {
                        Matcher matcher = pattern.matcher(userInput);
                        if (matcher.find()) {
                            String match = matcher.group(1);
                            if (match != null) {
                                // 特殊处理某些参数类型
                                if (paramName.equals("otpValue")) {
                                    boolean otpValue = match.equals("开启") || match.equals("打开") || 
                                                     match.equals("启用") || match.equals("激活") || 
                                                     match.equals("true");
                                    mergedParams.put(paramName, otpValue);
                                } else {
                                    mergedParams.put(paramName, match);
                                }
                            }
                        }
                    }
                }
            }
            
            // 设置合并后的参数
            bankResult.setParameters(mergedParams);
            
            // 设置调用次数（保持与第一次解析一致）
            bankResult.setCallCount(firstResult.getCallCount());
            
            return bankResult;
        } else {
            return createUnknownResult("不支持的事件库类型");
        }
    }
    
    /**
     * 第三次解析：根据第二次解析结果，解析关联的标准事件
     */
    public IntentRecognitionResult thirdParse(IntentRecognitionResult secondResult) {
        if (secondResult == null) {
            return createUnknownResult("第二次解析结果为空");
        }

        if (secondResult.getLibraryType() != EventLibraryType.BANK) {
            return createUnknownResult("第二次解析结果必须是银行事件");
        }
        
        // 获取银行事件配置
        EventLibraryConfig bankConfig = bankEventLibrary.get(secondResult.getIntentName());
        if (bankConfig == null) {
            return createUnknownResult("未找到银行事件配置");
        }
        
        // 获取关联的标准事件
        List<String> standardEvents = bankConfig.getRelatedEventsList();
        if (standardEvents.isEmpty()) {
            return createUnknownResult("银行事件未关联标准事件");
        }
        
        // 创建标准事件结果，完全基于第二次解析的结果
        IntentRecognitionResult finalResult = new IntentRecognitionResult();
        // 复制第二次解析的所有基本信息
        finalResult.setParameters(secondResult.getParameters());
        finalResult.setCallCount(secondResult.getCallCount());
        finalResult.setConfidence(secondResult.getConfidence());
        finalResult.setDescription(secondResult.getDescription());
        
        // 设置标准事件特定信息
        finalResult.setLibraryType(EventLibraryType.STANDARD);
        finalResult.setIntentName(standardEvents.get(0));  // 使用第一个标准事件作为主事件
        finalResult.setRelatedEvents(standardEvents);  // 设置关联的标准事件列表
        
        return finalResult;
    }
    
    /**
     * 匹配事件库
     */
    private IntentRecognitionResult matchEventLibrary(String userInput, 
                                                   Map<String, EventLibraryConfig> library,
                                                   EventLibraryType libraryType) {
        for (Map.Entry<String, EventLibraryConfig> entry : library.entrySet()) {
            String eventName = entry.getKey();
            EventLibraryConfig config = entry.getValue();
            
            for (Pattern pattern : config.getPatterns()) {
                Matcher matcher = pattern.matcher(userInput);
                if (matcher.matches()) {
                    // 创建匹配结果
                    IntentRecognitionResult result = new IntentRecognitionResult();
                    result.setIntentName(eventName);
                    result.setLibraryType(libraryType);
                    result.setDescription(config.getDescription());
                    result.setRelatedEvents(config.getRelatedEventsList());
                    result.setConfidence(70); // 基础置信度
                    
                    // 提取客户姓名参数
                    List<String> customerNames = extractCustomerNames(userInput);
                    if (!customerNames.isEmpty()) {
                        result.addParameter("customerNames", customerNames);
                        result.setCallCount(customerNames.size());
                    }
                    
                    // 尝试提取特定参数
                    extractParameters(userInput, result);
                    
                    return result;
                }
            }
        }
        
        return createUnknownResult("未匹配到" + libraryType.name() + "事件库中的任何意图");
    }
    
    /**
     * 提取参数值
     */
    private void extractParameters(String userInput, IntentRecognitionResult result) {
        // 提取OTP值参数
        Pattern otpValuePattern = commonValuePatterns.get("otpValue");
        if (otpValuePattern != null) {
            Matcher matcher = otpValuePattern.matcher(userInput);
            if (matcher.find()) {
                String match = matcher.group(1);
                boolean otpValue = match.equals("开启") || match.equals("打开") || 
                                  match.equals("启用") || match.equals("激活") || 
                                  match.equals("true");
                result.addParameter("otpValue", otpValue);
            }
        }
        
        // 可以在这里添加更多参数提取逻辑
    }
    
    /**
     * 提取客户名称
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
    
    /**
     * 创建未知结果
     */
    private IntentRecognitionResult createUnknownResult(String reason) {
        IntentRecognitionResult result = new IntentRecognitionResult();
        result.setIntentName("UNKNOWN");
        result.setLibraryType(EventLibraryType.STANDARD);
        result.setConfidence(0);
        result.setDescription("未能识别用户意图: " + reason);
        return result;
    }
} 
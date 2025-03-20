package com.example.chat.service;

import com.example.common.client.ServiceClient;
import com.example.common.model.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ServiceClient serviceClient;
    
    // 存储对话记录的简单实现
    private List<Map<String, String>> conversations = new ArrayList<>();
    
    // 用于异步处理上传文件的线程池
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public ChatResponse processUserInput(String userInput) {
        try {
            // 1. 分析用户意图
            String apiName = serviceClient.analyzeIntent(userInput);
            log.info("分析用户意图: {} -> {}", userInput, apiName);
            
            // 2. 执行API
            String result = serviceClient.executeApi(apiName, userInput);
            log.info("API执行结果: {}", result);
            
            // 3. 保存对话记录
            saveConversation(userInput, result);
            
            // 4. 返回结果
            return new ChatResponse(result);
        } catch (Exception e) {
            log.error("处理用户输入时发生错误", e);
            return new ChatResponse("抱歉，处理您的请求时发生错误：" + e.getMessage());
        }
    }
    
    /**
     * 处理用户输入并返回字符串结果
     */
    public String processUserInputAsString(String userInput) {
        try {
            // 1. 分析用户意图
            String apiName = serviceClient.analyzeIntent(userInput);
            log.info("分析用户意图: {} -> {}", userInput, apiName);
            
            // 2. 执行API
            String result = serviceClient.executeApi(apiName, userInput);
            log.info("API执行结果: {}", result);
            
            // 3. 保存对话记录
            saveConversation(userInput, result);
            
            // 4. 返回结果
            return result;
        } catch (Exception e) {
            log.error("处理用户输入时发生错误", e);
            return "抱歉，处理您的请求时发生错误：" + e.getMessage();
        }
    }
    
    /**
     * 获取所有对话记录
     */
    public List<Map<String, String>> getAllConversations() {
        return conversations;
    }
    
    /**
     * 分析用户意图
     */
    public String analyzeIntent(String userInput) {
        try {
            return serviceClient.analyzeIntent(userInput);
        } catch (Exception e) {
            log.error("分析用户意图失败", e);
            return "unknown";
        }
    }
    
    /**
     * 准备API执行参数
     */
    public Map<String, Object> prepareParameters(String userInput, MultipartFile file) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("userInput", userInput);
        
        if (file != null && !file.isEmpty()) {
            try {
                parameters.put("fileName", file.getOriginalFilename());
                parameters.put("fileContent", file.getBytes());
            } catch (Exception e) {
                log.error("处理上传文件失败", e);
            }
        }
        
        return parameters;
    }
    
    /**
     * 执行API
     */
    public String executeApi(String apiName, Map<String, Object> parameters) {
        try {
            // 从参数中获取userInput
            String userInput = (String) parameters.get("userInput");
            return serviceClient.executeApi(apiName, userInput);
        } catch (Exception e) {
            log.error("执行API失败: {}", apiName, e);
            return "执行操作失败：" + e.getMessage();
        }
    }
    
    /**
     * 保存对话记录
     */
    public void saveConversation(String userInput, String result) {
        Map<String, String> conversation = new HashMap<>();
        conversation.put("userInput", userInput);
        conversation.put("result", result);
        conversation.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        conversations.add(conversation);
        
        try {
            // 调用对话记录服务保存记录
            serviceClient.saveConversation(userInput, result);
        } catch (Exception e) {
            log.error("保存对话记录失败", e);
        }
    }
    
    /**
     * 处理上传的批量文件
     */
    public String processUploadedFile(MultipartFile file, String userInstruction) throws Exception {
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".csv") && !filename.endsWith(".xlsx"))) {
            throw new IllegalArgumentException("仅支持CSV或Excel文件");
        }
        
        if (filename.endsWith(".csv")) {
            return processCSVFile(file, userInstruction);
        } else {
            return "Excel文件处理功能正在开发中，请使用CSV格式";
        }
    }
    
    /**
     * 处理CSV文件
     */
    private String processCSVFile(MultipartFile file, String userInstruction) throws Exception {
        List<Map<String, String>> results = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader());
            List<CSVRecord> records = csvParser.getRecords();
            
            // 分析用户指令的意图
            String baseApiName = serviceClient.analyzeIntent(userInstruction);
            
            // 为每条记录创建一个异步任务
            List<CompletableFuture<Map<String, String>>> futures = new ArrayList<>();
            
            for (CSVRecord record : records) {
                CompletableFuture<Map<String, String>> future = CompletableFuture.supplyAsync(() -> {
                    Map<String, String> result = new HashMap<>();
                    try {
                        // 获取客户名称
                        String customerName = record.get("客户名称");
                        if (customerName == null || customerName.trim().isEmpty()) {
                            throw new IllegalArgumentException("客户名称不能为空");
                        }
                        
                        // 根据用户指令和客户名称构建具体的操作指令
                        String specificInstruction = userInstruction.replace("用户", customerName)
                                                                 .replace("客户", customerName);
                        
                        // 执行API
                        String apiResult = serviceClient.executeApi(baseApiName, specificInstruction);
                        
                        // 保存记录
                        saveConversation(specificInstruction, apiResult);
                        
                        result.put("status", "成功");
                        result.put("customerName", customerName);
                        result.put("instruction", specificInstruction);
                        result.put("result", apiResult);
                        
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        log.error("处理CSV记录失败", e);
                        result.put("status", "失败");
                        result.put("customerName", record.get("客户名称"));
                        result.put("error", e.getMessage());
                        
                        errorCount.incrementAndGet();
                    }
                    return result;
                }, executorService);
                
                futures.add(future);
            }
            
            // 等待所有任务完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            
            // 收集结果
            for (CompletableFuture<Map<String, String>> future : futures) {
                results.add(future.get());
            }
        }
        
        StringBuilder resultMessage = new StringBuilder();
        resultMessage.append(String.format("批量处理完成。总计: %d, 成功: %d, 失败: %d\n", 
            successCount.get() + errorCount.get(), successCount.get(), errorCount.get()));
        
        // 添加失败的详细信息
        if (errorCount.get() > 0) {
            resultMessage.append("\n失败详情：\n");
            results.stream()
                  .filter(r -> "失败".equals(r.get("status")))
                  .forEach(r -> resultMessage.append(String.format("客户 %s: %s\n", 
                      r.get("customerName"), r.get("error"))));
        }
        
        return resultMessage.toString();
    }
} 
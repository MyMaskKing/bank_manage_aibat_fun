package com.example.intent.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
public class IntentService {

    private final Map<String, Pattern> intentPatterns = new HashMap<>();
    
    public IntentService() {
        // 初始化意图匹配模式
        intentPatterns.put("updateOtpFlg", 
            Pattern.compile(".*(修改|更新|变更|启用|禁用|开启|关闭|设置).*(OTP|otp|一次性密码|动态密码|验证码).*"));
        
        intentPatterns.put("queryOtpStatus", 
            Pattern.compile(".*(查询|查看|获取|了解|检查).*(OTP|otp|一次性密码|动态密码|验证码).*"));
        
        intentPatterns.put("queryCustomer", 
            Pattern.compile(".*(查询|查看|获取|了解|检查).*(客户|用户|账户|会员).*"));
    }

    public String analyzeIntent(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            log.warn("接收到空的用户输入");
            return "unknown";
        }
        
        log.info("分析用户意图: {}", userInput);
        
        // 尝试根据正则表达式匹配意图
        for (Map.Entry<String, Pattern> entry : intentPatterns.entrySet()) {
            if (entry.getValue().matcher(userInput).matches()) {
                String intent = entry.getKey();
                log.info("匹配到意图: {}", intent);
                return intent;
            }
        }
        
        // 简单关键词匹配作为备用
        userInput = userInput.toLowerCase();
        
        if (userInput.contains("更新") && userInput.contains("otp")) {
            return "updateOtpFlg";
        } else if (userInput.contains("查询") && userInput.contains("otp")) {
            return "queryOtpStatus";
        } else if (userInput.contains("查询") && userInput.contains("客户")) {
            return "queryCustomer";
        }
        
        log.warn("未能识别用户意图: {}", userInput);
        return "unknown";
    }
} 
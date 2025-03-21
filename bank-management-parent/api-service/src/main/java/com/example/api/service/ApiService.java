package com.example.api.service;

import com.example.api.repository.CustomerRepository;
import com.example.common.model.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class ApiService {
    
    private final CustomerRepository customerRepository;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Map<String, ApiHandler> apiHandlers = new HashMap<>();
    
    public ApiService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        
        // 注册所有API处理器
        apiHandlers.put("queryCustomer", this::handleQueryCustomer);
        apiHandlers.put("queryOtpStatus", this::handleQueryOtpStatus);
        apiHandlers.put("updateOtpFlg", this::handleUpdateOtpFlg);
        
        // 注册未实现的API处理器
        apiHandlers.put("queryAccountBalance", input -> handleUnimplementedApi("查询账户余额", input));
        apiHandlers.put("queryTransactionHistory", input -> handleUnimplementedApi("查询交易记录", input));
        apiHandlers.put("transferMoney", input -> handleUnimplementedApi("转账", input));
        apiHandlers.put("test_queryAccountBalance", input -> handleUnimplementedApi("查询账户余额", input));
        apiHandlers.put("test_queryTransactionHistory", input -> handleUnimplementedApi("查询交易记录", input));
        apiHandlers.put("test_transferMoney", input -> handleUnimplementedApi("转账", input));
    }
    
    /**
     * 执行指定的API
     * @param apiName API名称
     * @param userInput 用户输入
     * @return API执行结果
     */
    public String executeApi(String apiName, String userInput) {
        log.info("开始执行API: {}, 用户输入: {}", apiName, userInput);
        
        ApiHandler handler = apiHandlers.get(apiName);
        if (handler == null) {
            // 检查是否是测试中的API
            if (apiName.startsWith("test_")) {
                String actualApiName = apiName.substring(5);
                return String.format("该功能（%s）正在开发测试中，暂时无法使用。", actualApiName);
            }
            return String.format("该功能（%s）正在开发中，暂时无法使用。", apiName);
        }
        
        try {
            String result = handler.handle(userInput);
            log.info("API执行成功: {}, 结果: {}", apiName, result);
            return result;
        } catch (Exception e) {
            String error = "API执行失败: " + e.getMessage();
            log.error("API执行异常: " + apiName, e);
            return error;
        }
    }
    
    /**
     * API处理器接口
     */
    @FunctionalInterface
    private interface ApiHandler {
        String handle(String userInput) throws Exception;
    }
    
    /**
     * 查询客户信息
     */
    private String handleQueryCustomer(String userInput) throws Exception {
        // 从用户输入中提取客户名称
        String customerName = extractCustomerName(userInput);
        
        // 在数据库中查询客户
        List<Customer> customers = customerRepository.findByCustomerName(customerName);
        if (customers.isEmpty()) {
            return "未找到名为 " + customerName + " 的客户信息";
        }
        
        Customer customer = customers.get(0);
        String timestamp = LocalDateTime.now().format(dateTimeFormatter);
        
        return String.format(
            "客户信息查询结果（%s）:\n" +
            "- 客户ID: %d\n" +
            "- 客户名称: %s\n" +
            "- 开户日期: %s\n" +
            "- 账户余额: %.2f 元\n" +
            "- OTP状态: %s\n" +
            "- 最后更新: %s",
            timestamp,
            customer.getCustomerId(),
            customer.getCustomerName(),
            customer.getOpenDate().toString(),
            customer.getBalance(),
            customer.getOtpFlg() ? "已开启" : "未开启",
            timestamp
        );
    }
    
    /**
     * 查询OTP状态
     */
    private String handleQueryOtpStatus(String userInput) throws Exception {
        // 从用户输入中提取客户名称
        String customerName = extractCustomerName(userInput);
        
        // 在数据库中查询客户
        List<Customer> customers = customerRepository.findByCustomerName(customerName);
        if (customers.isEmpty()) {
            return "未找到名为 " + customerName + " 的客户信息";
        }
        
        Customer customer = customers.get(0);
        String timestamp = LocalDateTime.now().format(dateTimeFormatter);
        
        return String.format(
            "OTP状态查询结果（%s）:\n" +
            "- 客户ID: %d\n" +
            "- 客户名称: %s\n" +
            "- OTP状态: %s\n" +
            "- 最后更新: %s",
            timestamp,
            customer.getCustomerId(),
            customer.getCustomerName(),
            customer.getOtpFlg() ? "已开启" : "未开启",
            timestamp
        );
    }
    
    /**
     * 更新OTP标志
     */
    @Transactional
    private String handleUpdateOtpFlg(String userInput) throws Exception {
        // 从用户输入中提取客户名称
        String customerName = extractCustomerName(userInput);
        
        // 确定需要设置的OTP状态
        boolean newOtpStatus = userInput.contains("开启") || userInput.contains("启用");
        
        // 在数据库中查询客户
        List<Customer> customers = customerRepository.findByCustomerName(customerName);
        if (customers.isEmpty()) {
            return "未找到名为 " + customerName + " 的客户信息";
        }
        
        Customer customer = customers.get(0);
        
        // 更新OTP状态
        customer.setOtpFlg(newOtpStatus);
        customerRepository.save(customer);
        
        String timestamp = LocalDateTime.now().format(dateTimeFormatter);
        
        return String.format(
            "OTP状态更新结果（%s）:\n" +
            "- 客户ID: %d\n" +
            "- 客户名称: %s\n" +
            "- OTP状态: 已更新为\"%s\"\n" +
            "- 操作时间: %s\n" +
            "- 操作结果: 成功",
            timestamp,
            customer.getCustomerId(),
            customer.getCustomerName(),
            newOtpStatus ? "开启" : "关闭",
            timestamp
        );
    }
    
    /**
     * 从用户输入中提取客户名称
     */
    private String extractCustomerName(String userInput) {
        // 默认客户名称
        String defaultName = "张三";
        
        // 常见中文名字
        String[] commonNames = {"张三", "李四", "王五", "赵六", "陈七", "刘八", "杨九", "周十"};
        
        // 尝试从输入中提取名字
        for (String name : commonNames) {
            if (userInput.contains(name)) {
                return name;
            }
        }
        
        return defaultName;
    }
    
    /**
     * 处理未实现的API
     */
    private String handleUnimplementedApi(String apiName, String userInput) {
        String customerName = extractCustomerName(userInput);
        String timestamp = LocalDateTime.now().format(dateTimeFormatter);
        
        return String.format(
            "%s功能正在开发中（%s）\n" +
            "您要查询的客户是：%s\n" +
            "该功能预计将在后续版本中提供。",
            apiName,
            timestamp,
            customerName
        );
    }
} 
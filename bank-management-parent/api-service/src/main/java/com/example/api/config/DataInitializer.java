package com.example.api.config;

import com.example.api.repository.CustomerRepository;
import com.example.common.model.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final CustomerRepository customerRepository;
    private final Random random = new Random();

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {
            long count = customerRepository.count();
            if (count == 0) {
                log.info("初始化客户数据...");
                initCustomers();
            } else {
                log.info("数据库中已存在 {} 个客户记录", count);
            }
        };
    }
    
    private void initCustomers() {
        List<Customer> customers = Arrays.asList(
            createCustomer(1, "张三", true, 50000.0),
            createCustomer(2, "李四", false, 150000.0),
            createCustomer(3, "王五", true, 200000.0),
            createCustomer(4, "赵六", false, 35000.0),
            createCustomer(5, "陈七", true, 75000.0),
            createCustomer(6, "刘八", false, 125000.0),
            createCustomer(7, "杨九", true, 250000.0),
            createCustomer(8, "周十", false, 180000.0)
        );
        
        List<Customer> savedCustomers = customerRepository.saveAll(customers);
        log.info("成功初始化 {} 个客户记录", savedCustomers.size());
        
        // 打印初始化的数据
        savedCustomers.forEach(customer -> 
            log.info("客户: ID={}, 姓名={}, OTP状态={}, 余额={}元, 开户日期={}",
                customer.getCustomerId(),
                customer.getCustomerName(),
                customer.getOtpFlg() ? "已开启" : "未开启",
                customer.getBalance(),
                customer.getOpenDate())
        );
    }
    
    private Customer createCustomer(int id, String name, boolean otpFlg, double balance) {
        Customer customer = new Customer();
        customer.setCustomerId(id);
        customer.setCustomerName(name);
        customer.setOtpFlg(otpFlg);
        customer.setBalance(balance);
        
        // 创建随机开户日期：2020-01-01 到 2023-12-31 之间
        int year = 2020 + random.nextInt(4);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        customer.setOpenDate(LocalDate.of(year, month, day));
        
        return customer;
    }
} 
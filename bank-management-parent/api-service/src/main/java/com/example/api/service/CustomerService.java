package com.example.api.service;

import com.example.api.repository.CustomerRepository;
import com.example.common.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public String updateOtpFlg(List<Integer> customerIds, boolean newFlgValue) {
        List<Customer> customers = customerRepository.findByCustomerIdIn(customerIds);
        if (customers.isEmpty()) {
            return "未找到指定的客户";
        }

        customers.forEach(customer -> customer.setOtpFlg(newFlgValue));
        customerRepository.saveAll(customers);

        return String.format("已成功更新%d个客户的OTP Flg为%s", customers.size(), newFlgValue);
    }

    public String queryOtpStatus(Integer customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return "未找到指定的客户";
        }
        return String.format("客户%d的OTP状态为%s", customerId, customer.getOtpFlg());
    }

    public String queryCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null) {
            return "未找到指定的客户";
        }
        return String.format("客户信息：ID=%d, 姓名=%s, OTP状态=%s",
            customer.getCustomerId(),
            customer.getCustomerName(),
            customer.getOtpFlg());
    }

    @Transactional
    public void initTestData() {
        if (customerRepository.count() == 0) {
            customerRepository.saveAll(List.of(
                new Customer(1, "张三", false),
                new Customer(2, "李四", true),
                new Customer(3, "王五", false),
                new Customer(4, "赵六", true)
            ));
        }
    }
} 
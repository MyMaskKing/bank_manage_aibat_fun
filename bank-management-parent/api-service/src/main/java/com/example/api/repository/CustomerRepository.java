package com.example.api.repository;

import com.example.common.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    
    /**
     * 根据客户名称查找客户
     */
    List<Customer> findByCustomerName(String customerName);
    
    /**
     * 根据客户ID列表查找客户
     */
    List<Customer> findByCustomerIdIn(List<Integer> customerIds);
} 
package com.example.common.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;
    
    @Column(name = "customer_name", nullable = false)
    private String customerName;
    
    @Column(name = "otp_flg", nullable = false)
    private Boolean otpFlg = false;
    
    @Column(name = "balance")
    private Double balance;
    
    @Column(name = "open_date")
    private LocalDate openDate;
    
    public Customer(Integer customerId, String customerName, Boolean otpFlg) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.otpFlg = otpFlg;
    }
} 
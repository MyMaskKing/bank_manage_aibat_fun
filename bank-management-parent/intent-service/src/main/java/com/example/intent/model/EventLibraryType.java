package com.example.intent.model;

/**
 * 事件库类型枚举
 */
public enum EventLibraryType {
    /**
     * 个人事件库 - 由银行事件组合而成
     */
    PERSONAL,
    
    /**
     * 银行事件库 - 由标准事件组合而成
     */
    BANK,
    
    /**
     * 标准事件库 - 最基础的操作API
     */
    STANDARD
} 
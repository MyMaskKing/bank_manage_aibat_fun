package com.example.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    
    @GetMapping("/")
    public String homePage() {
        return "chat";
    }
    
    @GetMapping("/chat")
    public String chatPage() {
        return "chat";
    }
} 
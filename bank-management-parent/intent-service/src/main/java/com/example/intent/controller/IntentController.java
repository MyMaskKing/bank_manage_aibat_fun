package com.example.intent.controller;

import com.example.intent.service.IntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/intent")
public class IntentController {

    @Autowired
    private IntentService intentService;

    @PostMapping("/analyze")
    public Map<String, String> analyzeIntent(@RequestBody Map<String, String> request) {
        String userInput = request.get("userInput");
        String apiName = intentService.analyzeIntent(userInput);
        
        Map<String, String> response = new HashMap<>();
        response.put("apiName", apiName);
        return response;
    }
} 
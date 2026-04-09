package com.example.demo.controllers;

import com.example.demo.StuffData;
import com.example.demo.services.AIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/stuff")
public class StuffingController {
    private final AIService aiService;

    public StuffingController(AIService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/context")
    private StuffData getStuffingResponse(
            @RequestParam(value = "question", defaultValue = "What are the AI models came in year 2026 out of all Companies?")String question,
            @RequestParam(value = "stuffing") boolean stuffing) throws IOException {
        return aiService.getBasedOnContext(question,stuffing);
    }
}



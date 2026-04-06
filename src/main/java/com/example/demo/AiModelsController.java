package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/model")
public class AiModelsController {
    private AIService aiService;

    public AiModelsController(AIService aiService) {
        this.aiService= aiService;
    }

    @GetMapping("/genre")
    private String getModels(@RequestParam(value = "genre", defaultValue = "logical") String genre){
        return aiService.getGenreResponse(genre);
    }
}

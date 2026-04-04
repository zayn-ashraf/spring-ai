package com.example.demo;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final AIService aiService;

    public ChatController(AIService aiService) {
        this.aiService = aiService;
    }


    @GetMapping
    public ResponseEntity<String> chat(){
        String response = aiService.getAIResponse("Give five models of Gemini");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dad-jokes")
    public String getDadJokes(@RequestParam( value = "message", defaultValue = "give me a dad joke") String message){
        return aiService.getAIResponse(message);
    }

}

package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class AIService {
    private final ChatClient chatClient;

    public AIService(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    public String getAIResponse(String prompt){
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
}

package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

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

    public String getGenreResponse(String genre){
        String messge = """
                List 5 popular Ai models in {genre} along with their company name. If you don't know the answer,
                Just say "I don't know".
                """;
        PromptTemplate promptTemplate = new PromptTemplate(messge);
        Prompt prompt = promptTemplate.create(Map.of("genre",genre));

        return chatClient.prompt(prompt).call().content();
    }
}

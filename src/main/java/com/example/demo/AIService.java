package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.stereotype.Service;

import java.util.List;
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

    public List<String> getSongsResponse(String artist) {
        String message = """
                 Give me top 10 songs of {artist} based on the views. If you dont find them, just say didnt find.
               {format}
                """;
        ListOutputConverter converter = new ListOutputConverter();

        String format = converter.getFormat();

        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt = promptTemplate.create(Map.of("artist",artist,"format",format));
        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
        return converter.convert(response.getResult().getOutput().getText());
    }

    public Map<String, Object> getServices(String bank) {
        String message = """
                Generate a JSON for a bank.
                
                - "bank": {bank} \s
                - "services": list like Retail Banking, Investment Banking, etc.) \s
                - Each category should contain service name as key and short description as value \s
                
                Return only valid JSON.
                {format}
                """;
        MapOutputConverter converter = new MapOutputConverter();
        String format = converter.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt =  promptTemplate.create(Map.of("bank", bank, "format", format));

        return converter.convert(chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText());
    }


    public Bank getTheServices(String bank){
        String message = """
                Generate a JSON for a bank.
                
                - "bank": {bank} \s
                - "services": list like Retail Banking, Investment Banking, etc.) \s
                - Each category should contain service name as key and short description as value \s
                
                Return only valid JSON.
                {format}
                """;
        var converter = new BeanOutputConverter<>(Bank.class);
        String format = converter.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(message);
        Prompt prompt =  promptTemplate.create(Map.of("bank", bank, "format", format));

        return converter.convert(chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText());
    }
}

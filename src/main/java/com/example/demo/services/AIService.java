package com.example.demo.services;

import com.example.demo.Bank;
import com.example.demo.StuffData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIService {
    private final ChatClient chatClient;

    @Value("classpath:prompts/stuffing.txt")
    private Resource stuffResource;

    @Value("classpath:prompts/context.st")
    private Resource context;

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

        public StuffData getBasedOnContext(String question, boolean stuffing) throws IOException {

//        String templateString = """
//Answer the following question STRICTLY using only the provided CONTEXT. Do NOT use any external knowledge.
//Return result as JSON with key 'data' and value as list of model names. If no data is there consider the CONTEXT data.
//If no data found, return empty list.
//
//CONTEXT:
//{context}
//
//QUESTION:
//{question}
//""";
//        var converter = new BeanOutputConverter<>(StuffData.class);
//        String format = converter.getFormat();
//        PromptTemplate promptTemplate = new PromptTemplate(templateString);
//        Map<String, Object> map = new HashMap<>();
//        map.put("question",question);
//        if(stuffing){
//            String str = new String(
//                    stuffResource.getInputStream().readAllBytes(),
//                    StandardCharsets.UTF_8);
//            System.out.println(str);
//            map.put("context", new String(
//                    stuffResource.getInputStream().readAllBytes(),
//                    StandardCharsets.UTF_8));
//        }else{
//            map.put("context", "");
//        }
//        Prompt prompt = promptTemplate.create(map);
//        System.out.println(prompt);
//        return converter.convert(chatClient.prompt(prompt).call().chatResponse().getResult().getOutput().getText());
            // 1. Add {format} to your template so the converter's instructions are included
            String templateString = """
        Give me the list of ai models in 2026, use CONTEXT data if you dont find any data, the context data is the latest models till 2026.
        response should include key as "data" and the values as list of strings for the values of data.
        
        {format}
        
        CONTEXT:
        {context}
        
        QUESTION:
        {question}
        """;

            var converter = new BeanOutputConverter<>(StuffData.class);

            // 2. Prepare the map and read the resource ONLY ONCE
            Map<String, Object> map = new HashMap<>();
            map.put("question", question);
            map.put("format", converter.getFormat()); // Pass the format instructions!

            if (stuffing) {
                // Read into a variable so we don't exhaust the stream
                byte[] bytes = stuffResource.getInputStream().readAllBytes();
                String contextData = new String(bytes, StandardCharsets.UTF_8);
                map.put("context", contextData);
                System.out.println(contextData);
            } else {
                map.put("context", "No context provided.");
            }

            PromptTemplate promptTemplate = new PromptTemplate(templateString);
            Prompt prompt = promptTemplate.create(map);

            // 3. Execute and Convert
            String rawResponse = chatClient.prompt(prompt)
                    .call()
                    .chatResponse()
                    .getResult()
                    .getOutput()
                    .getText();

            // Debug: See what the AI actually sent back
            System.out.println("Raw AI Response: " + rawResponse);

            return converter.convert(rawResponse);
    }
}

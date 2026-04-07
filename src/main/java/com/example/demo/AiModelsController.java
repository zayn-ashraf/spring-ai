package com.example.demo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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

    @GetMapping("/songs")
    private List<String> getTop10Songs(@RequestParam(value = "artist", defaultValue = "Saad Lamjarred") String artist){
        return aiService.getSongsResponse(artist);
    }

    @GetMapping("/banks")
    private Map<String,Object> getBankingServices(@RequestParam(value = "bank", defaultValue = "Jp Morgan Chase")String bank){
        return aiService.getServices(bank);
    }

    @GetMapping("/thebanks")
    private Bank getServices(@RequestParam(value = "bank", defaultValue = "Jp Morgan Chase")String bank){
        return aiService.getTheServices(bank);
    }

}

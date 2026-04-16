package com.cx.asset.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cx.asset.dto.AiResponse;
import com.cx.asset.service.AiService;
import com.cx.asset.service.Assistant;
import com.cx.asset.service.StreamingAssistant;

import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

/**
 * This is an example of using an {@link AiService}, a high-level LangChain4j API.
 */
@RestController
public class AssistantController {
    
    private final AiService aiService;

    public AssistantController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/assistant")
    public AiResponse assistant(@RequestParam(value = "message", defaultValue = "What is the current time?") String message) {
        return aiService.chat(message);
    }

    
}

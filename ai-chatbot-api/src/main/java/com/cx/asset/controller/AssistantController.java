package com.cx.asset.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cx.asset.dto.AiResponse;
import com.cx.asset.service.AiService;


/**
 * This is an example of using an {@link AiService}, a high-level LangChain4j API.
 */
@RestController
@RequestMapping("/assistant")
public class AssistantController {
    
    private final AiService aiService;

    public AssistantController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("")
    public AiResponse assistant(@RequestParam String message, @RequestParam String sessionId) {
        return aiService.chatWithSession(message, sessionId);
    }

    @DeleteMapping("/session/{sessionId}")
    public String clearSession(@PathVariable String sessionId) {
        aiService.clearSession(sessionId);
        return "Session " + sessionId + " cleared.";
    }

    @GetMapping("/debug/sessions")
    public String activeSessions() {
        return "Active sessions in memory: " + aiService.getActiveSessionCount();
    }

}

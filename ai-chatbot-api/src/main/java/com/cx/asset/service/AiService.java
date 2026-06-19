package com.cx.asset.service;

import com.cx.asset.config.AssistantConfiguration;
import org.springframework.stereotype.Service;

import com.cx.asset.dto.AiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiService {

    private final ChatMemoryService chatMemoryService;
    private final AssistantConfiguration assistantConfiguration;
    private final ObjectMapper mapper = new ObjectMapper();

    public AiService(ChatMemoryService chatMemoryService,
                     AssistantConfiguration assistantConfiguration) {
        this.chatMemoryService = chatMemoryService;
        this.assistantConfiguration = assistantConfiguration;
    }

    public AiResponse chatWithSession(String message, String sessionId) {
        try {
            Assistant sessionAssistant = assistantConfiguration.getOrCreateAssistant(sessionId);
            String json = sessionAssistant.chat(message);
            AiResponse aiResponse = mapper.readValue(json, AiResponse.class);
            chatMemoryService.saveExchange(sessionId, message, aiResponse);

            return aiResponse;

        } catch (Exception e) {
            return new AiResponse("ERROR", "FAILED", null, "AI error: " + e.getMessage());
        }
    }

    public void clearSession(String sessionId) {
        assistantConfiguration.clearSession(sessionId);
    }

    public int getActiveSessionCount() {
        return assistantConfiguration.getActiveSessionCount();
    }
}

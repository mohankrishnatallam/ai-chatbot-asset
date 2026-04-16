package com.cx.asset.service;

import org.springframework.stereotype.Service;

import com.cx.asset.dto.AiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AiService {
	private final Assistant assistant;
    private final ObjectMapper mapper = new ObjectMapper();

    public AiService(Assistant assistant) {
        this.assistant = assistant;
    }

    public AiResponse chat(String message) {
        try {
            String json = assistant.chat(message);
            return mapper.readValue(json, AiResponse.class);
        } catch (Exception e) {
            return new AiResponse("ERROR", "FAILED", null, "Invalid AI response");
        }
    }
}

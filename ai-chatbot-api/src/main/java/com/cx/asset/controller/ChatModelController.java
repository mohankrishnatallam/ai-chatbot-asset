package com.cx.asset.controller;

import dev.langchain4j.model.chat.ChatModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is an example of using a {@link ChatModel}, a low-level LangChain4j API.
 */
@RestController
@Tag(name = "Chat Model", description = "Low-level LangChain4j chat model (no tools or session memory)")
public class ChatModelController {

    private final ChatModel chatModel;
    
    @Autowired
    private Environment env;
    
    public ChatModelController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }
    

    @PostConstruct
    public void checkEnv() {
        String key = env.getProperty("OPENAI_API_KEY");
        System.out.println("Key present: " + (key != null));
    }

    @GetMapping("/model")
    @Operation(summary = "Direct chat model call", description = "Simple GET with optional message query param. No tools or session.")
    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return chatModel.chat(message);
    }
}

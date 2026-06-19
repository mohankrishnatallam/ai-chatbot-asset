package com.cx.asset.controller;

import dev.langchain4j.model.chat.ChatModel;
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
    public String model(@RequestParam(value = "message", defaultValue = "Hello") String message) {
        return chatModel.chat(message);
    }
}

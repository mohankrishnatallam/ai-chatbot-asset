package com.cx.asset.config;

import com.cx.asset.listener.MyChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatModelListenerConfig {

    @Bean
    public ChatModelListener chatModelListener() {
        return new MyChatModelListener();
    }
}
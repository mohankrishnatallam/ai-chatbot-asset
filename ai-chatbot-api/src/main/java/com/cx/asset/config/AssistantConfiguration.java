package com.cx.asset.config;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.service.AiServices;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.cx.asset.controller.ChatModelController;
import com.cx.asset.listener.MyChatModelListener;
import com.cx.asset.service.Assistant;
import com.cx.asset.service.StreamingAssistant;
import com.cx.asset.tool.InventoryTools;
import com.cx.asset.tool.OrderTools;
import com.cx.asset.tool.ReportingTools;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class AssistantConfiguration {

    /**
     * This chat memory will be used by {@link Assistant} and {@link StreamingAssistant}
     */
    @Bean
    @Scope(SCOPE_PROTOTYPE)
    ChatMemory chatMemory() {
        return MessageWindowChatMemory.withMaxMessages(10);
    }

    /**
     * This listener will be injected into every {@link ChatModel} and {@link StreamingChatModel}
     * bean   found in the application context.
     * It will listen for {@link ChatModel} in the {@link ChatModelController} as well as
     * {@link Assistant} and {@link StreamingAssistant}.
     */
    @Bean
    ChatModelListener chatModelListener() {
        return new MyChatModelListener();
    }
    
       
   
    @Bean
    public Assistant assistant(ChatModel chatModel,
                               ChatMemory chatMemory,
                               OrderTools orderTools, InventoryTools inventoryTools, ReportingTools reportingTools) {

        return AiServices.builder(Assistant.class)
                .chatModel(chatModel)
                .chatMemory(chatMemory)
                .tools(orderTools,inventoryTools, reportingTools) // 👈 ADD TOOL HERE
                .build();
    }
}

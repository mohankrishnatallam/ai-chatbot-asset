package com.cx.asset.config;

import com.cx.asset.service.Assistant;
import com.cx.asset.tool.InventoryTools;
import com.cx.asset.tool.OrderTools;
import com.cx.asset.tool.ReportingTools;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.service.AiServices;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Configuration
public class AssistantConfiguration {

    /**
     * Each session gets its own Assistant with its own ChatMemory.
     */
    private final Map<String, Assistant> sessionAssistants = new ConcurrentHashMap<>();
    private final ChatModel chatModel;
    private final OrderTools orderTools;
    private final InventoryTools inventoryTools;
    private final ReportingTools reportingTools;

    public AssistantConfiguration(ChatModel chatModel, OrderTools orderTools, InventoryTools inventoryTools, ReportingTools reportingTools) {
        this.chatModel = chatModel;
        this.orderTools = orderTools;
        this.inventoryTools = inventoryTools;
        this.reportingTools = reportingTools;
    }

    /**
     * Returns existing Assistant for this session, or creates a new one.
     * <p>
     * computeIfAbsent is atomic — safe if two requests come at the same time
     * for the same sessionId.
     */
    public Assistant getOrCreateAssistant(String sessionId) {
        return sessionAssistants.computeIfAbsent(sessionId, id -> {
            // Each session gets its own independent ChatMemory (last 10 messages)
            ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

            return AiServices.builder(Assistant.class)
                    .chatModel(chatModel)
                    .chatMemory(memory)
                    .tools(orderTools, inventoryTools, reportingTools)
                    .build();
        });
    }

    /**
     * Call this when a session ends or user logs out.
     * Frees memory so the map doesn't grow forever.
     */
    public void clearSession(String sessionId) {
        sessionAssistants.remove(sessionId);
    }

    /**
     * Useful for admin/debug: how many active sessions are in memory?
     */
    public int getActiveSessionCount() {
        return sessionAssistants.size();
    }
}


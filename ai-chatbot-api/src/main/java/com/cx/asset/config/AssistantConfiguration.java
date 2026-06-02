package com.cx.asset.config;

import com.cx.asset.entity.ChatTurn;
import com.cx.asset.service.Assistant;
import com.cx.asset.service.ChatMemoryService;
import com.cx.asset.tool.InventoryTools;
import com.cx.asset.tool.OrderTools;
import com.cx.asset.tool.ReportingTools;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableScheduling
public class AssistantConfiguration {

    /**
     * Each session gets its own Assistant with its own ChatMemory.
     */
    private final Map<String, Assistant> sessionAssistants = new ConcurrentHashMap<>();
    private final Map<String, Instant> sessionLastActive = new ConcurrentHashMap<>();

    private final ChatModel chatModel;
    private final OrderTools orderTools;
    private final InventoryTools inventoryTools;
    private final ReportingTools reportingTools;
    private final ChatMemoryService chatMemoryService;

    public AssistantConfiguration(ChatModel chatModel,
                                  OrderTools orderTools,
                                  InventoryTools inventoryTools,
                                  ReportingTools reportingTools,
                                  ChatMemoryService chatMemoryService) {
        this.chatModel = chatModel;
        this.orderTools = orderTools;
        this.inventoryTools = inventoryTools;
        this.reportingTools = reportingTools;
        this.chatMemoryService = chatMemoryService;
    }

    public Assistant getOrCreateAssistant(String sessionId) {
        sessionLastActive.put(sessionId, Instant.now());

        return sessionAssistants.computeIfAbsent(sessionId, id -> {

            ChatMemory memory = MessageWindowChatMemory.withMaxMessages(20);

            List<ChatTurn> history = chatMemoryService.getHistory(sessionId);
            for (ChatTurn turn : history) {
                memory.add(UserMessage.from(turn.getQuestion()));
                memory.add(AiMessage.from(turn.getAnswerText()));
            }

            return AiServices.builder(Assistant.class)
                    .chatModel(chatModel)
                    .chatMemory(memory)
                    .tools(orderTools, inventoryTools, reportingTools)
                    .build();
        });
    }

    public void clearSession(String sessionId) {
        sessionAssistants.remove(sessionId);
        sessionLastActive.remove(sessionId);
    }

    public int getActiveSessionCount() {
        return sessionAssistants.size();
    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void cleanupIdleSessions() {
        Instant cutoff = Instant.now().minusSeconds(30 * 60);
        sessionLastActive.entrySet().removeIf(entry -> {
            if (entry.getValue().isBefore(cutoff)) {
                sessionAssistants.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }
}

package com.cx.asset.service;

import com.cx.asset.config.AssistantConfiguration;
import com.cx.asset.tool.InventoryTools;
import com.cx.asset.tool.OrderTools;
import org.springframework.stereotype.Service;

import com.cx.asset.dto.AiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

@Service
public class AiService {

    private final ChatMemoryService chatMemoryService;
    private final AssistantConfiguration assistantConfiguration;
    private final OrderTools orderTools;
    private final InventoryTools inventoryTools;
    private final ObjectMapper mapper = new ObjectMapper();

    public AiService(ChatMemoryService chatMemoryService,
                     AssistantConfiguration assistantConfiguration,
                     OrderTools orderTools,
                     InventoryTools inventoryTools) {
        this.chatMemoryService = chatMemoryService;
        this.assistantConfiguration = assistantConfiguration;
        this.orderTools = orderTools;
        this.inventoryTools = inventoryTools;
    }

    public AiResponse chatWithSession(String message, String sessionId, String userId) {
        try {
            SessionContext.set(sessionId, userId);
            chatMemoryService.validateSessionOwnershipIfExists(sessionId, userId);

            Optional<String> createdOrder = orderTools.tryCreateOrderFromMessage(message);
            if (createdOrder.isPresent()) {
                AiResponse aiResponse = toOrderResponse(createdOrder.get());
                chatMemoryService.saveExchange(sessionId, userId, message, aiResponse);
                assistantConfiguration.clearSession(sessionId);
                return aiResponse;
            }

            Optional<String> fulfilledOrder = orderTools.tryFulfillPendingOrder(message);
            if (fulfilledOrder.isPresent()) {
                AiResponse aiResponse = toOrderResponse(fulfilledOrder.get());
                chatMemoryService.saveExchange(sessionId, userId, message, aiResponse);
                assistantConfiguration.clearSession(sessionId);
                return aiResponse;
            }

            Optional<AiResponse> ordersResponse = orderTools.tryBuildOrdersResponse(message);
            if (ordersResponse.isPresent()) {
                AiResponse aiResponse = ordersResponse.get();
                chatMemoryService.saveExchange(sessionId, userId, message, aiResponse);
                return aiResponse;
            }

            Optional<AiResponse> inventoryResponse = inventoryTools.tryBuildInventoryResponse(message);
            if (inventoryResponse.isPresent()) {
                AiResponse aiResponse = inventoryResponse.get();
                chatMemoryService.saveExchange(sessionId, userId, message, aiResponse);
                return aiResponse;
            }

            Assistant sessionAssistant = assistantConfiguration.getOrCreateAssistant(sessionId);
            String json = sessionAssistant.chat(message);
            AiResponse aiResponse = mapper.readValue(json, AiResponse.class);
            chatMemoryService.saveExchange(sessionId, userId, message, aiResponse);

            return aiResponse;

        } catch (IllegalArgumentException e) {
            return new AiResponse("ERROR", "FAILED", null,
                    e.getMessage() != null ? e.getMessage() : "Invalid request.");
        } catch (Exception e) {
            String recoverable = AiErrorMapper.recoverableUserMessage(e, message);
            if (recoverable != null) {
                AiResponse aiResponse = new AiResponse("ORDER", "FAILED", null, recoverable);
                chatMemoryService.saveExchange(sessionId, userId, message, aiResponse);
                return aiResponse;
            }
            return new AiResponse("ERROR", "FAILED", null, "AI error: " + e.getMessage());
        } finally {
            SessionContext.clear();
        }
    }

    public void clearSession(String sessionId) {
        orderTools.clearPendingOrder(sessionId);
        assistantConfiguration.clearSession(sessionId);
    }

    public int getActiveSessionCount() {
        return assistantConfiguration.getActiveSessionCount();
    }

    private static AiResponse toOrderResponse(String message) {
        boolean success = message != null && message.toLowerCase().startsWith("order created successfully");
        return new AiResponse("ORDER", success ? "SUCCESS" : "FAILED", null, message);
    }
}

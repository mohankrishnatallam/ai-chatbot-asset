package com.cx.asset.controller;

import com.cx.asset.entity.ChatTurn;
import com.cx.asset.service.ChatMemoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cx.asset.dto.AiResponse;
import com.cx.asset.dto.ChatSessionSummary;
import com.cx.asset.service.AiService;

import java.util.List;
import java.util.Map;

/**
 * The UI sends {@code message} as a query param. {@code X-Session-Id} and {@code X-User-Id} are
 * mandatory request headers on assistant endpoints; missing or blank values return HTTP 400.
 */
@RestController
@RequestMapping("/assistant")
public class AssistantController {

    public static final String SESSION_ID_HEADER = "X-Session-Id";
    public static final String USER_ID_HEADER = "X-User-Id";

    private final AiService aiService;
    private final ChatMemoryService chatMemoryService;

    public AssistantController(AiService aiService, ChatMemoryService chatMemoryService) {
        this.aiService = aiService;
        this.chatMemoryService = chatMemoryService;
    }

    @GetMapping("")
    public AiResponse assistant(@RequestParam String message,
                                @RequestHeader(value = SESSION_ID_HEADER, required = true) String sessionId,
                                @RequestHeader(value = USER_ID_HEADER, required = true) String userId) {
        return aiService.chatWithSession(message,
                requireHeader(sessionId, SESSION_ID_HEADER),
                requireHeader(userId, USER_ID_HEADER));
    }

    @DeleteMapping("/session")
    public ResponseEntity<?> deleteSession(
            @RequestHeader(value = SESSION_ID_HEADER, required = true) String sessionId,
            @RequestHeader(value = USER_ID_HEADER, required = true) String userId) {
        try {
            sessionId = requireHeader(sessionId, SESSION_ID_HEADER);
            userId = requireHeader(userId, USER_ID_HEADER);
            chatMemoryService.deleteSession(sessionId, userId);
            aiService.clearSession(sessionId);
            return ResponseEntity.ok(Map.of("message", "Session deleted"));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @GetMapping("/debug/sessions")
    public String activeSessions() {
        return "Active sessions in memory: " + aiService.getActiveSessionCount();
    }

    @GetMapping("/history")
    public List<ChatTurn> getHistory(
            @RequestHeader(value = SESSION_ID_HEADER, required = true) String sessionId,
            @RequestHeader(value = USER_ID_HEADER, required = true) String userId) {
        sessionId = requireHeader(sessionId, SESSION_ID_HEADER);
        userId = requireHeader(userId, USER_ID_HEADER);
        chatMemoryService.validateSessionAccess(sessionId, userId);
        return chatMemoryService.getHistory(sessionId);
    }

    @GetMapping("/sessions")
    public List<ChatSessionSummary> getUserSessions(@RequestParam String userId) {
        return chatMemoryService.getSessionsForUser(userId);
    }

    private static String requireHeader(String value, String headerName) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, headerName + " header is required.");
        }
        return value.trim();
    }
}

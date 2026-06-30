package com.cx.asset.controller;

import com.cx.asset.entity.ChatTurn;
import com.cx.asset.service.ChatMemoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cx.asset.dto.AiResponse;
import com.cx.asset.dto.AssistantChatRequest;
import com.cx.asset.dto.ChatSessionSummary;
import com.cx.asset.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

import static com.cx.asset.controller.ApiHeaders.SESSION_ID_HEADER;
import static com.cx.asset.controller.ApiHeaders.USER_ID_HEADER;

/**
 * The UI sends {@code message} in the JSON request body. {@code X-Session-Id} and {@code X-User-Id} are
 * mandatory request headers on assistant endpoints; missing or blank values return HTTP 400.
 */
@RestController
@RequestMapping("/assistant")
@Tag(name = "Assistant", description = "AI assistant chat, session history, and session management")
public class AssistantController {

    private final AiService aiService;
    private final ChatMemoryService chatMemoryService;

    public AssistantController(AiService aiService, ChatMemoryService chatMemoryService) {
        this.aiService = aiService;
        this.chatMemoryService = chatMemoryService;
    }

    @PostMapping("")
    @Operation(summary = "Send a chat message", description = "Requires X-Session-Id and X-User-Id headers. Body: { \"message\": \"...\" }")
    public AiResponse assistant(@RequestBody AssistantChatRequest request,
                                @RequestHeader(value = SESSION_ID_HEADER, required = true) String sessionId,
                                @RequestHeader(value = USER_ID_HEADER, required = true) String userId) {
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message is required.");
        }

        return aiService.chatWithSession(request.getMessage().trim(),
                requireHeader(sessionId, SESSION_ID_HEADER),
                requireHeader(userId, USER_ID_HEADER));
    }

    @DeleteMapping("/session")
    @Operation(summary = "Delete a chat session and its turns")
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
    @Operation(summary = "Count active in-memory assistant sessions")
    public String activeSessions() {
        return "Active sessions in memory: " + aiService.getActiveSessionCount();
    }

    @GetMapping("/history")
    @Operation(summary = "Get chat turns for a session")
    public List<ChatTurn> getHistory(
            @RequestHeader(value = SESSION_ID_HEADER, required = true) String sessionId,
            @RequestHeader(value = USER_ID_HEADER, required = true) String userId) {
        sessionId = requireHeader(sessionId, SESSION_ID_HEADER);
        userId = requireHeader(userId, USER_ID_HEADER);
        chatMemoryService.validateSessionOwnershipIfExists(sessionId, userId);
        return chatMemoryService.getHistory(sessionId);
    }

    @DeleteMapping("/history/truncate")
    @Operation(summary = "Truncate session history after a turn sequence")
    public ResponseEntity<?> truncateHistory(
            @RequestHeader(value = SESSION_ID_HEADER, required = true) String sessionId,
            @RequestHeader(value = USER_ID_HEADER, required = true) String userId,
            @RequestParam int afterSequence) {
        try {
            sessionId = requireHeader(sessionId, SESSION_ID_HEADER);
            userId = requireHeader(userId, USER_ID_HEADER);
            chatMemoryService.truncateHistoryAfter(sessionId, userId, afterSequence);
            aiService.clearSession(sessionId);
            return ResponseEntity.ok(Map.of("message", "History truncated"));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @GetMapping("/sessions")
    @Operation(summary = "List chat sessions for the logged-in user")
    public List<ChatSessionSummary> getUserSessions(
            @RequestHeader(value = USER_ID_HEADER, required = true) String userId) {
        return chatMemoryService.getSessionsForUser(requireHeader(userId, USER_ID_HEADER));
    }

    private static String requireHeader(String value, String headerName) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, headerName + " header is required.");
        }
        return value.trim();
    }
}

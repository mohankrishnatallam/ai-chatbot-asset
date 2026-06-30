package com.cx.asset.service;

import com.cx.asset.dto.AiResponse;
import com.cx.asset.dto.ChatSessionSummary;
import com.cx.asset.entity.ChatSession;
import com.cx.asset.entity.ChatTurn;
import com.cx.asset.repository.ChatSessionRepository;
import com.cx.asset.repository.ChatTurnRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMemoryService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatTurnRepository chatTurnRepository;

    public ChatMemoryService(ChatSessionRepository chatSessionRepository,
                             ChatTurnRepository chatTurnRepository) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatTurnRepository = chatTurnRepository;
    }

    public void saveExchange(String sessionId, String userId, String question, AiResponse aiResponse) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseGet(() -> {
                    String title = question.length() > 50
                            ? question.substring(0, 50) + "..."
                            : question;
                    ChatSession newSession = new ChatSession(title);
                    newSession.setId(sessionId);
                    newSession.setUserId(userId);
                    return chatSessionRepository.save(newSession);
                });

        if (userId != null && !userId.isBlank() && session.getUserId() == null) {
            session.setUserId(userId);
        }

        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);

        int sequence = chatTurnRepository.countBySessionId(sessionId);
        ChatTurn turn = new ChatTurn(sessionId, question, aiResponse, sequence);
        chatTurnRepository.save(turn);
    }

    public List<ChatTurn> getHistory(String sessionId) {
        return chatTurnRepository.findBySessionIdOrderBySequenceAsc(sessionId);
    }

    public List<ChatSessionSummary> getSessionsForUser(String userId) {
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(session -> new ChatSessionSummary(
                        session.getId(),
                        session.getTitle(),
                        session.getUpdatedAt(),
                        chatTurnRepository.countBySessionId(session.getId())
                ))
                .toList();
    }

    /**
     * Validates ownership when a session already exists. New sessions (not yet persisted)
     * are allowed so the first chat message can create them via {@link #saveExchange}.
     */
    public void validateSessionOwnershipIfExists(String sessionId, String userId) {
        chatSessionRepository.findById(sessionId).ifPresent(session -> {
            if (session.getUserId() != null && userId != null && !session.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Not allowed to access this session");
            }
        });
    }

    public void validateSessionAccess(String sessionId, String userId) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.getUserId() != null && userId != null && !session.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Not allowed to access this session");
        }
    }

    public void deleteSession(String sessionId, String userId) {
        validateSessionAccess(sessionId, userId);
        chatTurnRepository.deleteBySessionId(sessionId);
        chatSessionRepository.deleteById(sessionId);
    }

    /**
     * Removes turns after {@code lastSequenceInclusive} so the user can continue the chat
     * from an earlier point in the conversation.
     */
    public void truncateHistoryAfter(String sessionId, String userId, int lastSequenceInclusive) {
        validateSessionAccess(sessionId, userId);
        chatTurnRepository.deleteBySessionIdAndSequenceGreaterThan(sessionId, lastSequenceInclusive);

        chatSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setUpdatedAt(LocalDateTime.now());
            chatSessionRepository.save(session);
        });
    }
}

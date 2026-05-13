package com.cx.asset.service;

import com.cx.asset.dto.AiResponse;
import com.cx.asset.entity.ChatMessage;
import com.cx.asset.entity.ChatSession;
import com.cx.asset.entity.ChatTurn;
import com.cx.asset.repository.ChatSessionRepository;
import com.cx.asset.repository.ChatTurnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    /**
     * Saves one complete exchange (question + AI response) to chat_turns.
     */
    public void saveExchange(String sessionId, String question, AiResponse aiResponse) {
        ChatSession session = chatSessionRepository.findById(sessionId)
                .orElseGet(() -> {
                    String title = question.length() > 50
                            ? question.substring(0, 50) + "..."
                            : question;
                    ChatSession s = new ChatSession(title);
                    s.setId(sessionId);
                    return chatSessionRepository.save(s);
                });

        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);
        int sequence = chatTurnRepository.countBySessionId(sessionId);
        ChatTurn turn = new ChatTurn(sessionId, question, aiResponse, sequence);
        chatTurnRepository.save(turn);
    }

    /**
     * Returns all turns for a session in order.
     * This is to show conversation history in the UI.
     */
    public List<ChatTurn> getTurns(String sessionId) {
        return chatTurnRepository.findBySessionIdOrderBySequenceAsc(sessionId);
    }

}
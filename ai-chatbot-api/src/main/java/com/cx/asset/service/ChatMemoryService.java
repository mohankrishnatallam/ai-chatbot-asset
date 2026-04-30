package com.cx.asset.service;

import com.cx.asset.entity.ChatMessage;
import com.cx.asset.entity.ChatSession;
import com.cx.asset.repository.ChatSessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChatMemoryService {

    private final ChatSessionRepository chatSessionRepository;

    public ChatMemoryService(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    public void saveExchange(String sessionId, String userMessage, String aiResponse) {
        ChatSession session = chatSessionRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    ChatSession s = new ChatSession();
                    s.setSessionId(sessionId);
                    s.setCreatedAt(LocalDateTime.now());
                    s.setMessages(new ArrayList<>());
                    return s;
                });

        List<ChatMessage> msgs = session.getMessages();
        if (msgs == null) msgs = new ArrayList<>();

        msgs.add(new ChatMessage("USER", userMessage));
        msgs.add(new ChatMessage("AI", aiResponse));

        session.setMessages(msgs);
        chatSessionRepository.save(session);
    }

    public List<ChatMessage> getMessages(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId)
                .map(ChatSession::getMessages)
                .orElse(new ArrayList<>());
    }

}
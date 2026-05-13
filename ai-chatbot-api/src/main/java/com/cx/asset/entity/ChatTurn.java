package com.cx.asset.entity;

import com.cx.asset.dto.AiResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "chat_turns")
public class ChatTurn {

    @Id
    private String id;

    private String sessionId;

    private String question;

    private String answerText;

    private AiResponse assistantPayload;

    private int sequence;

    private LocalDateTime createdAt;

    public ChatTurn() {}

    public ChatTurn(String sessionId, String question, AiResponse assistantPayload, int sequence) {
        this.sessionId = sessionId;
        this.question = question;
        this.answerText = assistantPayload.getMessage();
        this.assistantPayload = assistantPayload;
        this.sequence = sequence;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }

    public AiResponse getAssistantPayload() { return assistantPayload; }
    public void setAssistantPayload(AiResponse assistantPayload) { this.assistantPayload = assistantPayload; }

    public int getSequence() { return sequence; }
    public void setSequence(int sequence) { this.sequence = sequence; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

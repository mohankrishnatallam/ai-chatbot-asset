package com.cx.asset.dto;

import java.time.LocalDateTime;

public class ChatSessionSummary {

    private String sessionId;
    private String title;
    private LocalDateTime updatedAt;
    private int turnCount;

    public ChatSessionSummary() {}

    public ChatSessionSummary(String sessionId, String title, LocalDateTime updatedAt, int turnCount) {
        this.sessionId = sessionId;
        this.title = title;
        this.updatedAt = updatedAt;
        this.turnCount = turnCount;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public void setTurnCount(int turnCount) {
        this.turnCount = turnCount;
    }
}

package com.cx.asset.dto;

public class SavePromptRequest {

    private String userId;
    private String text;

    public SavePromptRequest() {}

    public SavePromptRequest(String userId, String text) {
        this.userId = userId;
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

package com.cx.asset.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "chat_sessions")
public class ChatSession {

    @Id
    private String id;

    private String userId;

    private String title;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ChatSession(String title) {
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

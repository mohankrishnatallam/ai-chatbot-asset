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
@Document(collection = "saved_prompts")
public class SavedPrompt {

    @Id
    private String id;

    private String userId;

    private String text;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public SavedPrompt(String userId, String text) {
        this.userId = userId;
        this.text = text;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}

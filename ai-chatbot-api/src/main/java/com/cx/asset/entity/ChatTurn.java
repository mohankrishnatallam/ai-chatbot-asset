package com.cx.asset.entity;

import com.cx.asset.dto.AiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
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

    public ChatTurn(String sessionId, String question, AiResponse assistantPayload, int sequence) {
        this.sessionId = sessionId;
        this.question = question;
        this.answerText = assistantPayload.getMessage();
        this.assistantPayload = assistantPayload;
        this.sequence = sequence;
        this.createdAt = LocalDateTime.now();
    }
}

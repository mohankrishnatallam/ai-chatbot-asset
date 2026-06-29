package com.cx.asset.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionSummary {

    private String sessionId;
    private String title;
    private LocalDateTime updatedAt;
    private int turnCount;
}

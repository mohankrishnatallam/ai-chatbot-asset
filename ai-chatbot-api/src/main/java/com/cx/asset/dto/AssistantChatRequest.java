package com.cx.asset.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Assistant chat message")
public class AssistantChatRequest {

    @Schema(example = "Create an order with product 23456 - 2 on shipping address Texas")
    private String message;
}

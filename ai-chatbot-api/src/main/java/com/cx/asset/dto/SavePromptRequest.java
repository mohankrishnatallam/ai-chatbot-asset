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
@Schema(description = "Save prompt text")
public class SavePromptRequest {

    @Schema(example = "Generate a sales report for last month")
    private String text;
}

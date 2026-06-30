package com.cx.asset.controller;

import com.cx.asset.dto.SavePromptRequest;
import com.cx.asset.entity.SavedPrompt;
import com.cx.asset.service.SavedPromptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static com.cx.asset.controller.ApiHeaders.USER_ID_HEADER;

@RestController
@RequestMapping("/prompts")
@Tag(name = "Saved Prompts", description = "User saved prompt templates")
public class SavedPromptController {

    private final SavedPromptService savedPromptService;

    public SavedPromptController(SavedPromptService savedPromptService) {
        this.savedPromptService = savedPromptService;
    }

    @GetMapping("")
    @Operation(summary = "List saved prompts for the user")
    public List<SavedPrompt> getUserPrompts(
            @RequestHeader(value = USER_ID_HEADER, required = true) String userId) {
        return savedPromptService.getPromptsForUser(requireHeader(userId, USER_ID_HEADER));
    }

    @PostMapping("")
    @Operation(summary = "Save a new prompt")
    public ResponseEntity<?> savePrompt(
            @RequestHeader(value = USER_ID_HEADER, required = true) String userId,
            @RequestBody SavePromptRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(errorBody("Request body is required"));
        }

        try {
            SavedPrompt savedPrompt = savedPromptService.savePrompt(
                    requireHeader(userId, USER_ID_HEADER),
                    request.getText());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPrompt);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(errorBody(exception.getMessage()));
        }
    }

    @DeleteMapping("/{promptId}")
    @Operation(summary = "Delete a saved prompt")
    public ResponseEntity<?> deletePrompt(
            @PathVariable String promptId,
            @RequestHeader(value = USER_ID_HEADER, required = true) String userId) {
        try {
            savedPromptService.deletePrompt(promptId, requireHeader(userId, USER_ID_HEADER));
            return ResponseEntity.ok(errorBody("Prompt deleted"));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(errorBody(exception.getMessage()));
        }
    }

    private static String requireHeader(String value, String headerName) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, headerName + " header is required.");
        }
        return value.trim();
    }

    private Map<String, String> errorBody(String message) {
        return Map.of("message", message);
    }
}

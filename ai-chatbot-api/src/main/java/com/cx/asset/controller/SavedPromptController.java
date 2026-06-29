package com.cx.asset.controller;

import com.cx.asset.dto.SavePromptRequest;
import com.cx.asset.entity.SavedPrompt;
import com.cx.asset.service.SavedPromptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/prompts")
public class SavedPromptController {

    private final SavedPromptService savedPromptService;

    public SavedPromptController(SavedPromptService savedPromptService) {
        this.savedPromptService = savedPromptService;
    }

    @GetMapping("")
    public List<SavedPrompt> getUserPrompts(@RequestParam String userId) {
        return savedPromptService.getPromptsForUser(userId);
    }

    @PostMapping("")
    public ResponseEntity<?> savePrompt(@RequestBody SavePromptRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body(errorBody("Request body is required"));
        }

        try {
            SavedPrompt savedPrompt = savedPromptService.savePrompt(request.getUserId(), request.getText());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPrompt);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(errorBody(exception.getMessage()));
        }
    }

    @DeleteMapping("/{promptId}")
    public ResponseEntity<?> deletePrompt(@PathVariable String promptId, @RequestParam String userId) {
        try {
            savedPromptService.deletePrompt(promptId, userId);
            return ResponseEntity.ok(errorBody("Prompt deleted"));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(errorBody(exception.getMessage()));
        }
    }

    private Map<String, String> errorBody(String message) {
        return Map.of("message", message);
    }
}

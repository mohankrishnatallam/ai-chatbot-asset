package com.cx.asset.service;

import com.cx.asset.entity.SavedPrompt;
import com.cx.asset.repository.SavedPromptRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedPromptService {

    private final SavedPromptRepository savedPromptRepository;

    public SavedPromptService(SavedPromptRepository savedPromptRepository) {
        this.savedPromptRepository = savedPromptRepository;
    }

    public List<SavedPrompt> getPromptsForUser(String userId) {
        return savedPromptRepository.findByUserIdOrderByUpdatedAtDesc(userId);
    }

    public SavedPrompt savePrompt(String userId, String text) {
        String normalizedText = text == null ? "" : text.trim();

        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User id is required");
        }

        if (normalizedText.isEmpty()) {
            throw new IllegalArgumentException("Prompt text is required");
        }

        return savedPromptRepository.save(new SavedPrompt(userId, normalizedText));
    }

    public void deletePrompt(String promptId, String userId) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User id is required");
        }

        SavedPrompt prompt = savedPromptRepository.findById(promptId)
                .orElseThrow(() -> new IllegalArgumentException("Prompt not found"));

        if (!userId.equals(prompt.getUserId())) {
            throw new IllegalArgumentException("Not allowed to delete this prompt");
        }

        savedPromptRepository.delete(prompt);
    }
}

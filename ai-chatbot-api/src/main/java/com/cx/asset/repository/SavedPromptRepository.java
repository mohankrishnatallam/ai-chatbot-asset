package com.cx.asset.repository;

import com.cx.asset.entity.SavedPrompt;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SavedPromptRepository extends MongoRepository<SavedPrompt, String> {

    List<SavedPrompt> findByUserIdOrderByUpdatedAtDesc(String userId);
}

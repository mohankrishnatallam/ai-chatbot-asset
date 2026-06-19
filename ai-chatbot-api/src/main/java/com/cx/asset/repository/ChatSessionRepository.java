package com.cx.asset.repository;

import com.cx.asset.entity.ChatSession;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {
    Optional<ChatSession> findByUserId(String userId);
}
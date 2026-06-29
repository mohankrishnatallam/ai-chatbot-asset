package com.cx.asset.repository;

import com.cx.asset.entity.ChatTurn;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatTurnRepository extends MongoRepository<ChatTurn, String> {

    List<ChatTurn> findBySessionIdOrderBySequenceAsc(String sessionId);

    int countBySessionId(String sessionId);

    void deleteBySessionId(String sessionId);
}

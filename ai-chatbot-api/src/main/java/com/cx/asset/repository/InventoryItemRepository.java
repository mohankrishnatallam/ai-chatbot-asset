package com.cx.asset.repository;

import com.cx.asset.entity.InventoryItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface InventoryItemRepository extends MongoRepository<InventoryItem, String> {

    Optional<InventoryItem> findByProductId(String productId);
}
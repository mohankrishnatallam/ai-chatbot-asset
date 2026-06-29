package com.cx.asset.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "inventory_items")
public class InventoryItem {

    @Id
    private String id;

    private String productId;

    private int stock;

    private int reserved;

    private boolean available;

    private LocalDateTime updatedAt;

    public InventoryItem(String productId, int stock) {
        this.productId = productId;
        this.stock = stock;
        this.reserved = 0;
        this.available = stock > 0;
        this.updatedAt = LocalDateTime.now();
    }
}

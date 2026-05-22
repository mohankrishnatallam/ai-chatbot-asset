package com.cx.asset.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "inventory_items")
public class InventoryItem {

    @Id
    private String id;

    private String productId;

    private int stock;

    private int reserved;

    private boolean available;

    private LocalDateTime updatedAt;

    public InventoryItem() {}

    public InventoryItem(String productId, int stock) {
        this.productId = productId;
        this.stock = stock;
        this.reserved = 0;
        this.available = stock > 0;
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getReserved() { return reserved; }
    public void setReserved(int reserved) { this.reserved = reserved; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
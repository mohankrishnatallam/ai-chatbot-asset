package com.cx.asset.tool;

import com.cx.asset.entity.InventoryItem;
import com.cx.asset.repository.InventoryItemRepository;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class InventoryTools {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryTools(InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
    }

    @Tool("""
            Get the full inventory catalog from inventory_items.
            Each item includes productId, productName, stock, reserved, and available quantity.
            Products are global — not filtered by sessionId or userId.
            """)
    public String getInventoryDetails() {
        List<InventoryItem> items = inventoryItemRepository.findAll();
        if (items.isEmpty()) {
            return "No products found in inventory.";
        }

        StringBuilder details = new StringBuilder("Inventory (all products):\n");
        for (int i = 0; i < items.size(); i++) {
            details.append(i + 1).append(". ").append(formatItem(items.get(i)));
            if (i < items.size() - 1) {
                details.append("\n");
            }
        }
        return details.toString();
    }

    @Tool("Check stock availability for a product by productId from inventory_items")
    public String checkInventory(String productId) {
        Optional<InventoryItem> itemOpt = findByProductId(productId);
        if (itemOpt.isEmpty()) {
            return "Product ID " + productId + " not found in inventory.";
        }
        return formatItem(itemOpt.get());
    }

    @Tool("Reserve stock for a product by productId from inventory_items")
    public String reserveStock(String productId, int quantity) {
        Optional<InventoryItem> itemOpt = findByProductId(productId);
        if (itemOpt.isEmpty()) {
            return "Failed to reserve stock: product ID " + productId + " not found in inventory.";
        }

        InventoryItem item = itemOpt.get();
        int available = availableStock(item);
        if (available < quantity) {
            return "Failed to reserve stock for product ID " + productId
                    + ". Available: " + available + ", requested: " + quantity + ".";
        }

        item.setReserved(item.getReserved() + quantity);
        item.setAvailable(item.getStock() - item.getReserved() > 0);
        item.setUpdatedAt(LocalDateTime.now());
        inventoryItemRepository.save(item);

        return "Stock reserved successfully. Product ID: " + productId
                + " | Reserved: " + quantity
                + " | Remaining available: " + (available - quantity);
    }

    private Optional<InventoryItem> findByProductId(String productId) {
        if (productId == null || productId.isBlank()) {
            return Optional.empty();
        }
        return inventoryItemRepository.findByProductId(productId.trim());
    }

    private int availableStock(InventoryItem item) {
        return item.getStock() - item.getReserved();
    }

    private String formatItem(InventoryItem item) {
        StringBuilder details = new StringBuilder();
        details.append("Product ID: ").append(item.getProductId());
        if (item.getProductName() != null && !item.getProductName().isBlank()) {
            details.append(" | Name: ").append(item.getProductName());
        }
        details.append(" | Stock: ").append(item.getStock())
                .append(" | Reserved: ").append(item.getReserved())
                .append(" | Available: ").append(availableStock(item));
        return details.toString();
    }
}

package com.cx.asset.tool;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.langchain4j.agent.tool.Tool;

@Component
public class InventoryTools {

	 @Tool("Check inventory")
	    public Map<String, Object> checkInventory(String productId) {
	        return Map.of(
	                "productId", productId,
	                "available", true,
	                "stock", 50
	        );
	    }

	    @Tool("Reserve stock")
	    public Map<String, Object> reserveStock(String productId, int quantity) {
	        return Map.of(
	                "productId", productId,
	                "reserved", quantity
	        );
	    }
}

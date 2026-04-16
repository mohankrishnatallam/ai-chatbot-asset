package com.cx.asset.tool;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.langchain4j.agent.tool.Tool;

@Component
public class OrderTools {

	@Tool("Create a new order with product name and quantity")
    public String createOrder(String productName, int quantity) {

        // Your real business logic here (DB / service call)
        String orderId = "ORD-" + System.currentTimeMillis();

        return "Order created successfully. Order ID: " + orderId +
               ", Product: " + productName +
               ", Quantity: " + quantity;
    }
	
	 @Tool("Create new order")
	    public Map<String, Object> createNewOrder(String customerId, String productId, int quantity, String shippingAddress) {
	        return Map.of(
	                "orderId", "ORD-" + System.currentTimeMillis(),
	                "status", "CREATED",
	                "totalPrice", quantity * 100
	        );
	    }

	    @Tool("Cancel order")
	    public Map<String, Object> cancelOrder(String orderId) {
	        return Map.of(
	                "orderId", orderId,
	                "status", "CANCELLED"
	        );
	    }
}

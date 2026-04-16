package com.cx.asset.tool;

import java.util.Map;

import org.springframework.stereotype.Component;

import dev.langchain4j.agent.tool.Tool;

@Component
public class ReportingTools {
	   @Tool("Sales report")
	    public Map<String, Object> generateSalesReport() {
	        return Map.of(
	                "totalOrders", 120,
	                "revenue", 50000
	        );
	    }

	    @Tool("Inventory report")
	    public Map<String, Object> generateInventoryReport() {
	        return Map.of(
	                "totalProducts", 500,
	                "lowStock", 20
	        );
	    }
}

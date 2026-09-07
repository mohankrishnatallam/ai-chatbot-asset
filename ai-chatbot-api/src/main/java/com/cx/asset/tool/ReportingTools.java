package com.cx.asset.tool;

import com.cx.asset.config.MongoChartsProperties;
import com.cx.asset.enums.ReportType;
import com.cx.asset.service.SessionContext;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class ReportingTools {

    private final MongoChartsProperties chartsProperties;

    public ReportingTools(MongoChartsProperties chartsProperties) {
        this.chartsProperties = chartsProperties;
    }

    @Tool("Sales report")
    public Map<String, Object> generateSalesReport() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("totalOrders", 120);
        payload.put("revenue", 50000);
        addEmbedIfConfigured(payload, ReportType.SALES);
        return payload;
    }

    @Tool("Inventory report")
    public Map<String, Object> generateInventoryReport() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("totalProducts", 500);
        payload.put("lowStock", 20);
        addEmbedIfConfigured(payload, ReportType.INVENTORY);
        return payload;
    }

    private void addEmbedIfConfigured(Map<String, Object> payload, ReportType reportType) {
        if (reportType == ReportType.SALES && !chartsProperties.isSalesConfigured()) {
            return;
        }
        if (reportType == ReportType.INVENTORY && !chartsProperties.isInventoryConfigured()) {
            return;
        }

        Map<String, Object> embed = new LinkedHashMap<>();
        embed.put("kind", "dashboard");
        embed.put("baseUrl", chartsProperties.getBaseUrl());
        embed.put("dashboardId", reportType == ReportType.SALES
                ? chartsProperties.getSalesDashboardId()
                : chartsProperties.getInventoryDashboardId());
        embed.put("filter", salesFilter(reportType));
        payload.put("embed", embed);
    }

    private Map<String, Object> salesFilter(ReportType reportType) {
        if (reportType != ReportType.SALES) {
            return Map.of();
        }

        String userId = SessionContext.getUserId();
        return userId == null || userId.isBlank() ? Map.of() : Map.of("userId", userId);
    }
}

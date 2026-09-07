package com.cx.asset.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MongoChartsProperties {

    @Value("${mongodb.charts.base-url:}")
    private String baseUrl;

    @Value("${mongodb.charts.sales-dashboard-id:}")
    private String salesDashboardId;

    @Value("${mongodb.charts.inventory-dashboard-id:}")
    private String inventoryDashboardId;

    @Value("${mongodb.charts.embed-secret:}")
    private String embedSecret;

    @Value("${mongodb.charts.token-audience:ai-chatbot-asset}")
    private String tokenAudience;

    @Value("${mongodb.charts.token-ttl-seconds:3600}")
    private long tokenTtlSeconds;

    public String getBaseUrl() {
        return trimToEmpty(baseUrl);
    }

    public String getSalesDashboardId() {
        return trimToEmpty(salesDashboardId);
    }

    public String getInventoryDashboardId() {
        return trimToEmpty(inventoryDashboardId);
    }

    public String getEmbedSecret() {
        return embedSecret == null ? "" : embedSecret;
    }

    public String getTokenAudience() {
        return trimToEmpty(tokenAudience);
    }

    public long getTokenTtlSeconds() {
        return tokenTtlSeconds > 0 ? tokenTtlSeconds : 3600;
    }

    public boolean isTokenSigningConfigured() {
        return !getBaseUrl().isEmpty()
                && getEmbedSecret().getBytes(StandardCharsets.UTF_8).length >= 32;
    }

    public boolean isSalesConfigured() {
        return isTokenSigningConfigured() && !getSalesDashboardId().isEmpty();
    }

    public boolean isInventoryConfigured() {
        return isTokenSigningConfigured() && !getInventoryDashboardId().isEmpty();
    }

    public String missingConfigurationMessage() {
        return "MongoDB Atlas Charts is not configured. Set MONGODB_CHARTS_BASE_URL, "
                + "MONGODB_CHARTS_SALES_DASHBOARD_ID, MONGODB_CHARTS_INVENTORY_DASHBOARD_ID, "
                + "and MONGODB_CHARTS_EMBED_SECRET (at least 32 characters).";
    }

    private static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}

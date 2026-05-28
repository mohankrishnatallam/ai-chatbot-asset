package com.cx.asset.entity;

import com.cx.asset.enums.ReportType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "report_snapshots")
public class ReportSnapshot {

    @Id
    private String id;

    private ReportType reportType;

    private Map<String, Object> payload;

    private LocalDateTime generatedAt;

    private String requestedByUserId;



    public ReportSnapshot() {}

    public ReportSnapshot(ReportType reportType, Map<String, Object> payload) {
        this.reportType = reportType;
        this.payload = payload;
        this.generatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public String getRequestedByUserId() { return requestedByUserId; }
    public void setRequestedByUserId(String requestedByUserId) { this.requestedByUserId = requestedByUserId; }
}
package com.cx.asset.entity;

import com.cx.asset.enums.ReportType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "report_snapshots")
public class ReportSnapshot {

    @Id
    private String id;

    private ReportType reportType;

    private Map<String, Object> payload;

    private LocalDateTime generatedAt;

    private String requestedByUserId;

    public ReportSnapshot(ReportType reportType, Map<String, Object> payload) {
        this.reportType = reportType;
        this.payload = payload;
        this.generatedAt = LocalDateTime.now();
    }
}

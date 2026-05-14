package com.cx.asset.repository;

import com.cx.asset.entity.ReportSnapshot;
import com.cx.asset.enums.ReportType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportSnapshotRepository extends MongoRepository<ReportSnapshot, String> {

    List<ReportSnapshot> findByReportTypeOrderByGeneratedAtDesc(ReportType reportType);
}
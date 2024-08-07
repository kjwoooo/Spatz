package com.elice.spatz.domain.userfeature.repository;

import com.elice.spatz.domain.userfeature.model.entity.Report;
import com.elice.spatz.domain.userfeature.model.entity.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findAllByReporterIdAndReportStatus(Long reporterId, ReportStatus reportStatus, Pageable pageable);
    Page<Report> findAllByReportStatus(ReportStatus reportStatus, Pageable pageable);
    Optional<Report> findByReporterIdAndReportedIdAndReportStatus(Long reporterId, Long reportedId, ReportStatus reportStatus);
}

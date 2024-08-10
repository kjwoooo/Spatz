package com.elice.spatz.domain.userfeature.repository;

import com.elice.spatz.domain.userfeature.entity.Report;
import com.elice.spatz.domain.userfeature.entity.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findAllByReportStatus(ReportStatus reportStatus, Pageable pageable);
    Page<Report> findAllByReporterIdAndReportStatus(Long reporterId, ReportStatus reportStatus, Pageable pageable);
    Page<Report> findAllByReporterIdAndReportStatusNot(Long reporterId, ReportStatus reportStatus, Pageable pageable);
    Optional<Report> findByReporterIdAndReportedIdAndReportStatus(Long reporterId, Long reportedId, ReportStatus reportStatus);
    Optional<Report> findByIdAndReportStatusIn(Long reportId, List<ReportStatus> reportStatuses);
}

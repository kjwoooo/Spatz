package com.elice.spatz.domain.userfeature.controller;

import com.elice.spatz.domain.userfeature.dto.response.ReportDto;
import com.elice.spatz.domain.userfeature.entity.ReportStatus;
import com.elice.spatz.domain.userfeature.service.AdminFeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminFeatureController {
    private final AdminFeatureService adminFeatureService;

    // 1. 신고 목록 조회
    @GetMapping("/admin/reports")
    public ResponseEntity<Page<ReportDto>> getWaitingReports(@RequestParam ReportStatus reportStatus, @PageableDefault(page=0, size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<ReportDto>  waitingReportDtos = adminFeatureService.getWaitingReports(reportStatus, pageable);
        return ResponseEntity.ok(waitingReportDtos);
    }
    // 2. 신고 상세 조회
    @GetMapping("/admin/reports/{reportId}")
    public ResponseEntity<ReportDto> getReport(@PathVariable Long reportId) {
        ReportDto reportDto = adminFeatureService.getWaitingReport(reportId);
        return ResponseEntity.ok(reportDto);
    }
    // 3. 신고 응답
    @PatchMapping("/admin/reports/{reportId}")
    public ResponseEntity<String> responseReport(@PathVariable Long reportId, @RequestParam ReportStatus reportStatus) {
        adminFeatureService.responseReport(reportId, reportStatus);
        return ResponseEntity.ok("신고 응답이 완료되었습니다");
    }
}

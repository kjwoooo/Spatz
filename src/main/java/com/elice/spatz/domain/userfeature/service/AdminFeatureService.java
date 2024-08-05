package com.elice.spatz.domain.userfeature.service;

import com.elice.spatz.domain.user.entity.Users;
import com.elice.spatz.domain.user.repository.UserRepository;
import com.elice.spatz.domain.userfeature.model.dto.response.ReportDto;
import com.elice.spatz.domain.userfeature.model.dto.response.ResponseMapper;
import com.elice.spatz.domain.userfeature.model.entity.BannedUser;
import com.elice.spatz.domain.userfeature.model.entity.Report;
import com.elice.spatz.domain.userfeature.model.entity.ReportCount;
import com.elice.spatz.domain.userfeature.model.entity.ReportStatus;
import com.elice.spatz.domain.userfeature.repository.BannedUserRepository;
import com.elice.spatz.domain.userfeature.repository.ReportCountRepository;
import com.elice.spatz.domain.userfeature.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminFeatureService {
    private final ReportRepository reportRepository;
    private final ReportCountRepository reportCountRepository;
    private final ResponseMapper responseMapper;
    private final BannedUserRepository bannedUserRepository;
    private final UserRepository userRepository;

    // 1. 신고 목록 조회
    @Transactional
    public Page<ReportDto> getWaitingReports(ReportStatus reportStatus, Pageable pageable){
        Page<Report> waitingReports = reportRepository.findAllByReportStatus(reportStatus, pageable);
        List<ReportDto> waitingReportDtoList = waitingReports.getContent().stream()
                .map(responseMapper::reportToReportDto)
                .collect(Collectors.toList());
        return new PageImpl<>(waitingReportDtoList, pageable, waitingReports.getTotalElements());
    }
    // 2. 신고 상세 조회
    @Transactional
    public  ReportDto getWaitingReport(long reportId){
        Report report = reportRepository.findById(reportId).orElse(null);
        return responseMapper.reportToReportDto(report);
    }
    // 3. 신고 응답
    @Transactional
    public void responseReport(long reportId, ReportStatus reportStatus){
        Report report = reportRepository.findById(reportId).orElseThrow();

        report.setReportStatus(reportStatus);

        // 신고 수락시 누적 신고 횟수 추가
        if (reportStatus == ReportStatus.ACCEPTED){
            ReportCount reportCount = reportCountRepository.findByUserId(report.getReported().getId());
            if (reportCount == null) {
                ReportCount newReportCount = new ReportCount(report.getReported(), 1);
                reportCountRepository.save(newReportCount);
            }
            else {
                reportCount.setReportCount(reportCount.getReportCount()+1);
                // 누적 신고 횟수 3회이면 정지
                int reportedReportCount =  reportCount.getReportCount();
                if (reportedReportCount == 3){
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime oneMonthLater = now.plusMonths(1);

                    BannedUser newBannedUser = new BannedUser(report.getReported(), now, oneMonthLater);
                    bannedUserRepository.save(newBannedUser);
                }
            }
        }
    }
}

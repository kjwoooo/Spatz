package com.elice.spatz.domain.userfeature.dto.response;

import com.elice.spatz.domain.userfeature.entity.ReportStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReportDto {
    private Long id;
    private Long reporterId;
    private Long reportedId;
    private String reporterNickname;
    private String reportedNickname;
    private ReportStatus reportStatus;
    private String reportReason;
    private byte[] reportImage;
    private LocalDateTime createdAt;
}

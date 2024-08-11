package com.elice.spatz.domain.userfeature.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportCreateDto {
    private Long reporterId;
    private Long reportedId;
    private String reportReason;
}

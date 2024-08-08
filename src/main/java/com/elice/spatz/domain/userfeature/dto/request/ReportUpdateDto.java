package com.elice.spatz.domain.userfeature.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportUpdateDto {
    private long id;
    private long reporterId;
    private long reportedId;
    private String reportReason;
}

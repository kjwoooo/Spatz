package com.elice.spatz.domain.userfeature.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportUpdateDto {
    private long id;
    private String reportReason;
}

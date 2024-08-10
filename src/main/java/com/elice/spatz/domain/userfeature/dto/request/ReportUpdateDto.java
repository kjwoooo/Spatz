package com.elice.spatz.domain.userfeature.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReportUpdateDto {
    private Long id;
    private String reportReason;
}

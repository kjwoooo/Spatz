package com.elice.spatz.domain.userfeature.model.dto.response;

import com.elice.spatz.domain.userfeature.model.entity.ReportStatus;
import com.elice.spatz.domain.userfeature.model.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReportDto {
    private Long reporterId;
    private Long reportedId;
    private ReportStatus reportStatus;
    private String reportReason;
    private byte[] reportImage;
}

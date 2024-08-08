package com.elice.spatz.domain.userfeature.entity;

public enum ReportStatus {
    WAITING,
    ACCEPTED,
    INSUFFICIENT_EVIDENCE, // 증거 불충분
    INADEQUATE_REASON // 부적절한 사유
}

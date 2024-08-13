package com.elice.spatz.exception.errorCode;

import com.elice.spatz.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserFeatureErrorCode implements ErrorCode {
    // 공통
    NOT_FOUND_USER (HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다.", "UF-000"),

    // 친구 요청 응답, 신고 응답 공통
    ALREADY_RESPONSE(HttpStatus.BAD_REQUEST, "이미 응답이 완료되었습니다.", "UF-001"),

    // 차단
    ALREADY_BLOCKED (HttpStatus.BAD_REQUEST, "이미 차단이 완료된 사용자입니다.", "UF-B-001"),
    BLOCK_USER_SELF (HttpStatus.BAD_REQUEST, "자신을 차단할 수 없습니다.", "UF-B-002"),
    ALREADY_UNBLOCKED (HttpStatus.BAD_REQUEST, "이미 차단 해제가 완료되었습니다.", "UF-B-003"),

    // 친구요청, 관계
    BLOCKED_USER (HttpStatus.BAD_REQUEST, "차단 관계의 사용자입니다.", "UF-F-001"),
    BANNED_USER (HttpStatus.BAD_REQUEST, "정지된 사용자입니다.", "UF-F-002"),
    ALREADY_REQUESTED (HttpStatus.BAD_REQUEST, "이미 친구 요청을 보낸 사용자입니다. 응답할 때까지 조금만 기다려 주시기 바랍니다.", "UF-F-003"),
    ALREADY_RECEIVED (HttpStatus.BAD_REQUEST, "이미 상대방에게 친구 요청을 받았습니다. 친구 요청 탭을 확인해주시길 바랍니다.", "UF-F-004"),
    ALREADY_FRIEND(HttpStatus.BAD_REQUEST, "이미 친구 관계인 사용자입니다.", "UF-F-005"),
    ALREADY_UNFRIENDED(HttpStatus.BAD_REQUEST, "이미 친구 해제가 완료되었습니다.", "UF-F-006"),
    REQUEST_USER_SELF (HttpStatus.BAD_REQUEST, "자신에게 친구 요청을 전송할 수 없습니다.", "UF-B-007"),
    NOT_FOUND_USERS (HttpStatus.BAD_REQUEST, "입력하신 단어를 포함하는 닉네임을 가진 사용자가 없습니다.","UF-B-008"),

    // 신고
    ALREADY_REPORTED(HttpStatus.BAD_REQUEST, "동일 사용자에 대한 처리되지 않은 신고 내역이 존재합니다. 처리가 완료된 후 다시 시도해 주시기 바랍니다.", "UF-R-001"),
    NOT_FOUND_REPORT (HttpStatus.NOT_FOUND, "신고 정보를 찾을 수 없습니다.", "UF-R-002"),
    ALREADY_DELETED (HttpStatus.NOT_FOUND, "이미 삭제된 신고입니다.", "UF-R-003"),
    REPORT_USER_SELF (HttpStatus.BAD_REQUEST, "자신을 신고할 수 없습니다.", "UF-B-004");

    private final HttpStatus status;
    private final String message;
    private final String code;

    @Override
    public HttpStatus getHttpStatus() { return this.status; }

    @Override
    public String getMessage() { return this.message; }
}

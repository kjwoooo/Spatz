package com.elice.spatz.exception.handler;

import com.booksajo.bookPanda.exception.dto.ErrorResponse;
import com.elice.spatz.exception.ErrorCode;
import com.elice.spatz.exception.dto.ChannelErrorResponse;
import com.elice.spatz.exception.dto.UserErrorResponse;
import com.elice.spatz.exception.errorCode.UserErrorCode;
import com.elice.spatz.exception.errorCode.UserFeatureErrorCode;
import com.elice.spatz.exception.errorCode.VoiceChannelErrorCode;
import com.elice.spatz.exception.exception.ChatException;
import com.elice.spatz.exception.exception.ServerException;
import com.elice.spatz.exception.exception.UserException;
import com.elice.spatz.exception.exception.UserFeatureException;
import com.elice.spatz.exception.exception.VoiceChannelException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final SimpMessageSendingOperations messagingTemplate;

    public GlobalExceptionHandler(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    @ExceptionHandler(UserException.class)
    public ResponseEntity<UserErrorResponse> handleUserException(UserException ex)
    {
        UserErrorCode errorCode = ex.getUserErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new UserErrorResponse(errorCode.getHttpStatus().toString().split(" ")[0],
                        errorCode.getCode(),
                        errorCode.getMessage()));
    }

    @ExceptionHandler(ServerException.class)
    public ResponseEntity<String> handleServerException(ServerException ex)
    {
        ErrorCode errorCode = ex.getServerErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(errorCode.getMessage());
    }

    @ExceptionHandler(UserFeatureException.class)
    public ResponseEntity<UserErrorResponse> handleUserFeatureException(UserFeatureException ex)
    {
        UserFeatureErrorCode errorCode = ex.getUserFeatureErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(new UserErrorResponse(errorCode.getHttpStatus().toString().split(" ")[0],
                        errorCode.getCode(),
                        errorCode.getMessage()));
    }

    @ExceptionHandler(VoiceChannelException.class)
    public ResponseEntity<ChannelErrorResponse> handleUserFeatureException(VoiceChannelException ex)
    {
        VoiceChannelErrorCode errorCode = ex.getVoiceChannelErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus())
            .body(new ChannelErrorResponse(errorCode.getHttpStatus().toString().split(" ")[0],
                errorCode.getCode(),
                errorCode.getMessage()));
    }

    // 예외처리에 관한 http를 보내는 코드
    private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(makeErrorResponse(errorCode, errorCode.getMessage()));
    }

    private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(message)
                .build();
    }

    // 웹소켓에서 발생하는 예외처리
    @MessageExceptionHandler(ChatException.class)
    public void handleChatException(ChatException ex) {
        ErrorCode errorCode = ex.getChatErrorCode();
        ErrorResponse errorResponse = makeErrorResponse(errorCode, errorCode.getMessage());
        messagingTemplate.convertAndSend("/topic/errors", errorResponse);
    }

    // Bean validation 에서 입력값 검증 시 발생하는 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        StringBuilder errors = new StringBuilder();
//        ex.getBindingResult().getFieldErrors().forEach(error -> {
//            errors.append(error.getDefaultMessage());
//        });
        // 첫 번째 필드 에러만 가져오기
        String firstError = ex.getBindingResult().getFieldErrors().stream()
                .findFirst() // 첫 번째 에러를 찾아서
                .map(error -> error.getDefaultMessage())
                .orElse("Validation Error");

        return new ResponseEntity<>(firstError, HttpStatus.BAD_REQUEST);
    }


}

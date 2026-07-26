package com.suhsaechan.dongbanza.common.exception;

import com.suhsaechan.dongbanza.common.exception.dto.ErrorResponse;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // 커스텀 예외 처리
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    HttpStatus status = e.getErrorCode().getStatus();
    // 중복 이메일 같은 정상적인 클라이언트 오류(4xx)까지 ERROR로 남으면 진짜 장애가 묻힌다
    if (status.is5xxServerError()) {
      log.error("{}", e.getMessage());
    } else {
      log.warn("{}", e.getMessage());
    }
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());
    return ResponseEntity.status(status).body(errorResponse);
  }

  // 입력 검증 실패는 클라이언트 잘못이므로 500이 아닌 400으로 응답한다
  // (미처리 시 서비스 계층에서 NPE 등으로 터져 서버 오류로 둔갑함)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
    String detail = e.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
        .collect(Collectors.joining(", "));
    log.warn("입력 검증 실패 - {}", detail);
    return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getStatus())
        .body(new ErrorResponse(ErrorCode.INVALID_REQUEST, detail));
  }

  // 본문이 없거나 JSON 파싱 자체가 불가능한 요청도 400으로 처리한다
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleNotReadableException(HttpMessageNotReadableException e) {
    log.warn("요청 본문 파싱 실패 - {}", e.getMessage());
    return ResponseEntity.status(ErrorCode.INVALID_REQUEST.getStatus())
        .body(new ErrorResponse(ErrorCode.INVALID_REQUEST, ErrorCode.INVALID_REQUEST.getMessage()));
  }

  // 일반 예외 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    log.error("{}", e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
  }

}

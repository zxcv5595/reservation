package com.zxcv5595.reservation.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VALIDATION_FAILED("유효하지않은 값입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_EXIST_USER("이미 가입된 회원입니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;


}


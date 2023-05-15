package com.zxcv5595.reservation.exception;

import com.zxcv5595.reservation.type.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class CustomException extends RuntimeException {

    private final ErrorCode errorCode;
    private final int status;
    private final String message;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.status = errorCode.getStatus().value();
        this.message = errorCode.getMessage();
    }
}

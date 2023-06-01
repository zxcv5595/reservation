package com.zxcv5595.reservation.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    VALIDATION_FAILED("유효하지않은 값입니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_USER("해당하는 유저가 없습니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST_STORE("해당하는 가게가 없습니다.", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED("접근할 수 있는 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_MATCHED_PHONE("일치하는 예약이 없습니다.", HttpStatus.FORBIDDEN),
    RESERVATION_RESTRICTION("하루에 한 타임만 예약할 수 있습니다.", HttpStatus.FORBIDDEN),
    NOT_ACCEPTED_RESERVATION("수락되지않은 예약입니다.", HttpStatus.FORBIDDEN),
    NOT_VALID_RESERVATION("유효하지 않은 예약번호 입니다.", HttpStatus.FORBIDDEN),
    ALREADY_ACCEPTED_RESERVATION("이미 수락이 완료된 예약입니다.", HttpStatus.FORBIDDEN),
    NOT_VALID_TIME("예약 가능한 시간이 아닙니다.", HttpStatus.FORBIDDEN),
    ALREADY_WRITTEN_REVIEW("이미 리뷰를 작성했습니다.", HttpStatus.FORBIDDEN),
    ALREADY_REGISTER_OWNER("이미 owner에 등록되었습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_EXPIRED_RESERVATION("예약의 유효기간이 만료되었습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_VISITED_STORE("이미 방문처리 되었습니다.", HttpStatus.BAD_REQUEST),
    NOT_MATCHED_USER("예약정보와 유저가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    NOT_VISITED_STORE("방문하기 전에는 리뷰를 작성할 수 없습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_EXIST_STORE("이미 등록된 가게입니다.", HttpStatus.BAD_REQUEST),
    NOT_MATCHED_PASSWORD("비밀번호가 틀렸습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_EXIST_USER("이미 가입된 회원입니다.", HttpStatus.BAD_REQUEST);

    private final String message;
    private final HttpStatus status;


}


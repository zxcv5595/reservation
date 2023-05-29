package com.zxcv5595.reservation.service;

import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_EXPIRED_RESERVATION;
import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_VISITED_STORE;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_ACCEPTED_RESERVATION;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_MATCHED_PHONE;

import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.dto.VisitedReservation;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.ReservationListRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KioskService {

    private final ReservationListRepository reservationListRepository;

    @Transactional
    public void visitedReservation(VisitedReservation.Request request) {
        String phone = request.getPhone();
        Long storeId = request.getStoreId();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = LocalDateTime.of(today, LocalTime.MIN);
        LocalDateTime endOfToday = LocalDateTime.of(today, LocalTime.MAX);

        //예약 당일, 일치하는 예약정보가 있는지 확인
        ReservationList reservation = reservationListRepository.findByUserPhoneAndStoreIdAndReservationTimeBetween(
                        phone, storeId, startOfToday, endOfToday)
                .orElseThrow(() -> new CustomException(NOT_MATCHED_PHONE)); // 예약 정보

        checkVisit(reservation); // 방문확인 전 유효성 확인 코드

        reservation.setVisited(true); // 방문확인
    }

    private void checkVisit(ReservationList reservation) {
        if (reservation.getExpiredTime().isBefore(LocalDateTime.now())) { //만료기간 확인
            throw new CustomException(ALREADY_EXPIRED_RESERVATION);
        }
        if (!reservation.isPermission()) { //수락된 예약인지 확인
            throw new CustomException(NOT_ACCEPTED_RESERVATION);
        }
        if (reservation.isVisited()) { // 이미 방문처리되었는지 확인
            throw new CustomException(ALREADY_VISITED_STORE);
        }
    }


}

package com.zxcv5595.reservation.service;

import static com.zxcv5595.reservation.type.ErrorCode.NOT_ACCEPTED_RESERVATION;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_MATCHED_PHONE;

import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.ReservationListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KioskService {

    private final ReservationListRepository reservationListRepository;

    @Transactional
    public void visitedReservation(String phone) {
        ReservationList reservation = reservationListRepository.findByUserPhone(phone)
                .orElseThrow(() -> new CustomException(
                        NOT_MATCHED_PHONE)); // 예약 정보

        if (!reservation.isPermission()) { //수락된 예약인지 확인
            throw new CustomException(NOT_ACCEPTED_RESERVATION);
        }

        reservation.setVisited(true); //방문확인
    }


}

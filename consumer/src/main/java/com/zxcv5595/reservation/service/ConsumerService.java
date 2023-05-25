package com.zxcv5595.reservation.service;

import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_STORE;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_USER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_VALID_TIME;

import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.CreateReservation.Request;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.ReservationListRepository;
import com.zxcv5595.reservation.repository.StoreRepository;
import com.zxcv5595.reservation.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsumerService {

    private final StoreRepository storeRepository;
    private final ReservationListRepository reservationListRepository;
    private final UserRepository userRepository;

    public Page<Store> searchStoresNameByKeyword(String keyword, Pageable page) {
        return storeRepository.findByStoreNameContainingIgnoreCase(keyword, page);
    }

    public ReservationList createReservation(String username, Request request) {
        //예약할 유저
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));
        //예약할 가게
        Store store = storeRepository.findByStoreName(request.getStoreName())
                .orElseThrow(() -> new CustomException(
                        NOT_EXIST_STORE));
        //예약할 날짜,시간
        LocalDateTime reservationTime = LocalDateTime.of(request.getReservationDate(),
                request.getReservationTime());

        //예약시간 유효성확인
        validateReservationTime(reservationTime);

        //setting reservation
        ReservationList newReservation = ReservationList.builder()
                .user(user)
                .store(store)
                .reservationTime(reservationTime)
                .expiredTime(reservationTime.minusMinutes(10))//10분 전에 도착해야만 합니다.
                .permission(false)//승락여부 디폴트 false
                .visited(false)//방문확인 디폴트 false
                .build();

        //저장
        reservationListRepository.save(newReservation);

        return newReservation;

    }

    private void validateReservationTime(LocalDateTime reservationTime) {
        // 해당 예약 시간에 겹치는 예약이 있는지 확인
        List<ReservationList> existingReservations = reservationListRepository.findByReservationTime(
                reservationTime);

        //예약시간이 과거 이거나, 겹치는시간이 존재하면 예약불가
        if (reservationTime.isBefore(LocalDateTime.now()) || !existingReservations.isEmpty()) {
            throw new CustomException(NOT_VALID_TIME);
        }
    }


}

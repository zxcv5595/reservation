package com.zxcv5595.reservation.service;

import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_STORE;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_USER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_MATCHED_USER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_VALID_TIME;
import static com.zxcv5595.reservation.type.ErrorCode.RESERVATION_RESTRICTION;

import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Review;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.CreateReservation.Request;
import com.zxcv5595.reservation.dto.ReviewDto;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.ReservationListRepository;
import com.zxcv5595.reservation.repository.ReviewRepository;
import com.zxcv5595.reservation.repository.StoreRepository;
import com.zxcv5595.reservation.repository.UserRepository;
import com.zxcv5595.reservation.type.ErrorCode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
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
    private final ReviewRepository reviewRepository;

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
        validateReservationTime(reservationTime, store.getId());

        // 유저의 가게 당 하루에 1개의 예약 제한
        reservationRestriction(user, store,reservationTime);

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

    private void reservationRestriction(User user, Store store, LocalDateTime reservationTime) {
        LocalDate checkTime = reservationTime.toLocalDate();
        LocalDateTime startOfToday = LocalDateTime.of(checkTime, LocalTime.MIN);
        LocalDateTime endOfToday = LocalDateTime.of(checkTime, LocalTime.MAX);
        boolean hasReservationToday = reservationListRepository.existsByUserAndStoreAndReservationTimeBetween(
                user, store, startOfToday, endOfToday);
        if (hasReservationToday) {
            throw new CustomException(RESERVATION_RESTRICTION);
        }
    }

    public Review writeReview(User user, ReviewDto.Request request) {
        Long reservationId = request.getReservationId(); //예약번호
        ReservationList reservation = reservationListRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(
                        ErrorCode.NOT_VALID_RESERVATION));//예약정보

        validReservation(user, reservationId, reservation);

        //setting review
        Review newReview = Review.builder()
                .reservation(reservation)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        reviewRepository.save(newReview);

        return newReview;

    }

    private void validReservation(User user, Long reservationId, ReservationList reservation) {
        if (reviewRepository.existsByReservationId(reservationId)) {
            throw new CustomException(ErrorCode.ALREADY_WRITTEN_REVIEW);
        }
        if (!Objects.equals(reservation.getUser().getUsername(), user.getUsername())) {
            throw new CustomException(NOT_MATCHED_USER);
        } //유저정보, 예약정보 매칭확인
        if (!reservation.isVisited()) {
            throw new CustomException(ErrorCode.NOT_VISITED_STORE);
        }
    }

    private void validateReservationTime(LocalDateTime reservationTime, Long storeId) {
        // 해당 가게 그리고, 그 가게의 예약 시간에 겹치는 시간이 있는지 확인
        List<ReservationList> existingReservations = reservationListRepository.findByReservationTimeAndStoreId(
                reservationTime, storeId);

        //예약시간이 과거 이거나, 겹치는시간이 존재하면 예약불가
        if (reservationTime.isBefore(LocalDateTime.now()) || !existingReservations.isEmpty()) {
            throw new CustomException(NOT_VALID_TIME);
        }
    }


}

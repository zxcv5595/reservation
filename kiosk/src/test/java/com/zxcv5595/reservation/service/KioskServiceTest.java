package com.zxcv5595.reservation.service;

import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_EXPIRED_RESERVATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.VisitedReservation.Request;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.ReservationListRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class KioskServiceTest {

    @Mock
    private ReservationListRepository reservationListRepository;

    @InjectMocks
    private KioskService kioskService;


    @Test
    @DisplayName("방문처리")
    public void visitedReservation_ValidReservation_ShouldMarkVisited() {
        // given
        User currentUser = User.builder()
                .username("testUser")
                .password("password")
                .phone("010-1234-1234")
                .build();
        currentUser.setId(1L);

        Store store = Store.builder()
                .storeName("StoreName")
                .address("address")
                .description("description")
                .build();
        store.setId(1L);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reservationTime = now.plusHours(3); //2시간 전에는 예약 되어있어야함

        ReservationList validReservation = ReservationList.builder()
                .user(currentUser)
                .store(store)
                .reservationTime(reservationTime)
                .expiredTime(reservationTime.minusMinutes(10)) // 가게 10분전까지 와야함
                .permission(true)
                .visited(false)
                .build();
        validReservation.setId(1L);
        Request request = Request.builder()
                .storeId(1L)
                .phone("010-1234-1234")
                .build();

        doReturn(Optional.of(validReservation))
                .when(reservationListRepository)
                .findByUserPhoneAndStoreIdAndReservationTimeBetween(eq("010-1234-1234"), eq(1L),
                        any(LocalDateTime.class), any(LocalDateTime.class));

        // when
        kioskService.visitedReservation(request);

        // then
        Assertions.assertTrue(validReservation.isVisited());
    }

    @Test
    @DisplayName("방문처리 예외 - 예약만료")
    public void visitedReservation_ExpiredReservation_ShouldThrowException() {
        // given
        User currentUser = User.builder()
                .username("testUser")
                .password("password")
                .phone("010-1234-1234")
                .build();
        currentUser.setId(1L);

        Store store = Store.builder()
                .storeName("StoreName")
                .address("address")
                .description("description")
                .build();
        store.setId(1L);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime reservationTime = now.plusHours(3); //2시간 전에는 예약 되어있어야함

        ReservationList expiredReservation = ReservationList.builder()
                .user(currentUser)
                .store(store)
                .reservationTime(reservationTime)
                .expiredTime(reservationTime.minusHours(5)) // 가게 10분전까지 와야함- 만료된 상황
                .permission(true)
                .visited(false)
                .build();
        expiredReservation.setId(1L);
        Request request = Request.builder()
                .storeId(1L)
                .phone("010-1234-1234")
                .build();

        doReturn(Optional.of(expiredReservation))
                .when(reservationListRepository)
                .findByUserPhoneAndStoreIdAndReservationTimeBetween(eq("010-1234-1234"), eq(1L),
                        any(LocalDateTime.class), any(LocalDateTime.class));

        // Act and Assert
        CustomException exception = Assertions.assertThrows(CustomException.class, () ->
                kioskService.visitedReservation(request)
        );
        Assertions.assertFalse(expiredReservation.isVisited());
        Assertions.assertEquals(ALREADY_EXPIRED_RESERVATION.getMessage(), exception.getMessage());
    }

}
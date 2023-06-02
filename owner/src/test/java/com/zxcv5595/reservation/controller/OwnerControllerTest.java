package com.zxcv5595.reservation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.AddStore;
import com.zxcv5595.reservation.dto.ReservationDto;
import com.zxcv5595.reservation.service.OwnerService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OwnerControllerTest {

    @Mock
    private OwnerService ownerService;

    @InjectMocks
    private OwnerController ownerController;

    @Test
    @DisplayName("오너등록")
    void register_ShouldReturnOkResponse() {
        // given
        User currentUser = new User();
        currentUser.setUsername("testUser");

        // when
        ResponseEntity<String> response = ownerController.register(currentUser);

        // then
        verify(ownerService).register(eq("testUser"));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("등록이 완료되었습니다.", response.getBody());
    }

    @Test
    @DisplayName("가게 추가")
    void addStore_ShouldReturnOkResponse() {
        // Arrange
        User currentUser = new User();
        currentUser.setUsername("testUser");

        Store store = Store.builder()
                .storeName("StoreName")
                .address("address")
                .description("description")
                .build();

        AddStore.Request request = AddStore.Request.builder()
                .storeName("StoreName")
                .address("address")
                .description("description")
                .build();
        // Set request properties

        Owner owner = Owner.builder()
                .user(currentUser)
                .stores(new ArrayList<>())
                .build();

        owner.add(store);
        // Set owner properties

        when(ownerService.addStore(eq("testUser"), any(AddStore.Request.class))).thenReturn(owner);

        // Act
        ResponseEntity<AddStore.Response> response = ownerController.addStore(currentUser, request);

        // Assert
        verify(ownerService).addStore(eq("testUser"), eq(request));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertIterableEquals(AddStore.Response.from(owner).getStores(), Objects.requireNonNull(
                response.getBody()).getStores());

    }

    @Test
    @DisplayName("예약 조회")
    void getReservationByStoreId_ShouldReturnReservationList() {
        // given
        User currentUser = new User();
        currentUser.setUsername("testUser");

        Long storeId = 123L;
        Store store = Store.builder()
                .storeName("StoreName1")
                .build();
        store.setId(storeId);

        ReservationList reservation1 = ReservationList.builder()
                .reservationTime(LocalDateTime.now())
                .user(currentUser)
                .store(store)
                .build();
        // Set reservation1 properties

        ReservationList reservation2 = ReservationList.builder()
                .reservationTime(LocalDateTime.now().plusMinutes(30))
                .user(currentUser)
                .store(store)
                .build();
        // Set reservation2 properties

        List<ReservationList> reservations = new ArrayList<>();
        reservations.add(reservation1);
        reservations.add(reservation2);

        when(ownerService.getReservationsByStoreId(eq("testUser"), eq(storeId))).thenReturn(
                reservations);

        // when
        ResponseEntity<List<ReservationDto>> response = ownerController.getReservationByStoreId(
                currentUser, storeId);

        // then
        verify(ownerService).getReservationsByStoreId(eq("testUser"), eq(storeId));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reservations.size(), Objects.requireNonNull(response.getBody()).size());
    }

    @Test
    @DisplayName("예약 수락")
    void acceptReservation_ShouldReturnAcceptedReservation() {
        // given
        User currentUser = new User();
        currentUser.setUsername("testUser");

        Long storeId = 123L;
        Store store = Store.builder()
                .storeName("StoreName1")
                .build();
        store.setId(storeId);

        Long reservationId = 456L;

        ReservationList reservation = ReservationList.builder()
                .store(store)
                .reservationTime(LocalDateTime.now())
                .user(currentUser)
                .permission(true)
                .visited(false)
                .build();
        reservation.setId(reservationId);
        // Set reservation properties

        when(ownerService.acceptReservation(eq("testUser"), eq(reservationId))).thenReturn(reservation);

        // when
        ResponseEntity<ReservationDto> response = ownerController.acceptReservation(currentUser, reservationId);

        // then
        verify(ownerService).acceptReservation(eq("testUser"), eq(reservationId));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isPermission());
    }
}
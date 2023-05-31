package com.zxcv5595.reservation.service;

import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_ACCEPTED_RESERVATION;
import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_EXIST_STORE;
import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_EXPIRED_RESERVATION;
import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_REGISTER_OWNER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_STORE;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_USER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_VALID_RESERVATION;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.AddStore;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.OwnerRepository;
import com.zxcv5595.reservation.repository.ReservationListRepository;
import com.zxcv5595.reservation.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationListRepository reservationListRepository;

    @InjectMocks
    private OwnerService ownerService;

    @Test
    @DisplayName("오너등록 성공")
    public void testRegister_Successful() {
        // given
        String username = "username";
        User user = User.builder()
                .username("username")
                .password("password")
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(ownerRepository.existsByUser(user)).thenReturn(false); // 이미 OWNER에 등록되어있는지 확인

        // when
        assertDoesNotThrow(() -> ownerService.register(username));

        // then
        verify(userRepository, times(1)).findByUsername(username);
        verify(ownerRepository, times(1)).existsByUser(user);
        verify(ownerRepository, times(1)).save(any(Owner.class));
    }

    @Test
    @DisplayName("오너등록 에외 - 유저없음")
    public void testRegister_UserNotFound() {
        // given
        String username = "username";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> ownerService.register(username));
        assertEquals(NOT_EXIST_USER.getMessage(), exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    @DisplayName("오너등록 예외 - 이미 등록된 오너")
    public void testRegister_UserAlreadyRegisteredAsOwner() {
        // given
        String username = "username";
        User user = User.builder()
                .username("username")
                .password("password")
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(ownerRepository.existsByUser(user)).thenReturn(true);

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> ownerService.register(username));
        assertEquals(ALREADY_REGISTER_OWNER.getMessage(), exception.getMessage());

        verify(userRepository, times(1)).findByUsername(username);
        verify(ownerRepository, times(1)).existsByUser(user);
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    @DisplayName("가게추가 성공")
    public void testAddStore_Successful() {
        // given
        String username = "username";
        User user = User.builder()
                .username("username")
                .password("password")
                .build();
        Owner owner = Owner.builder()
                .user(user)
                .stores(new ArrayList<>())
                .build();

        when(ownerRepository.findByUserUsername(username)).thenReturn(Optional.of(owner));

        AddStore.Request request = new AddStore.Request("storeName", "address", "description");

        // when
        Owner result = ownerService.addStore(username, request);

        // Assert
        verify(ownerRepository, times(1)).findByUserUsername(username);
        assertNotNull(result);
        assertEquals(1, result.getStores().size());
    }

    @Test
    @DisplayName("가게추가 예외 - 유저없음")
    public void testAddStore_UserNotFound() {
        // given
        String username = "username";
        when(ownerRepository.findByUserUsername(username)).thenReturn(Optional.empty());

        AddStore.Request request = new AddStore.Request("storeName", "address", "description");

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> ownerService.addStore(username, request));
        assertEquals(NOT_EXIST_USER.getMessage(), exception.getMessage());

        verify(ownerRepository, times(1)).findByUserUsername(username);
    }

    @Test
    @DisplayName("가게추가 예외 - 중복된 가게이름")
    public void testAddStore_DuplicateStoreName() {
        // given
        String username = "username";
        User user = User.builder()
                .username("username")
                .password("password")
                .build();
        Owner owner = Owner.builder()
                .user(user)
                .stores(new ArrayList<>())
                .build();
        when(ownerRepository.findByUserUsername(username)).thenReturn(Optional.of(owner));

        AddStore.Request request = new AddStore.Request("storeName", "address", "description");

        Store existingStore = Store.builder()
                .storeName("storeName")
                .build();
        owner.add(existingStore); // 이름이 같은 가게, 미리 추가 해놓고 확인하기

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> ownerService.addStore(username, request));
        assertEquals(ALREADY_EXIST_STORE.getMessage(), exception.getMessage());

        verify(ownerRepository, times(1)).findByUserUsername(username);
    }

    @Test
    @DisplayName("가게 ID로 예약 조회 성공 ")
    public void testGetReservationsByStoreId_Successful() {
        // given
        String username = "username";
        Long storeId = 123L;
        Owner owner = new Owner();
        owner.setId(1L);
        Store store = Store.builder()
                .storeName("StoreName 1")
                .address("Address 1")
                .description("Description 1")
                .build();
        store.setId(storeId);
        owner.add(store);

        ReservationList reservation1 = new ReservationList();
        reservation1.setId(1L);
        reservation1.setStore(store);
        ReservationList reservation2 = new ReservationList();
        reservation2.setId(2L);
        reservation2.setStore(store);

        when(ownerRepository.findByUserUsername(username)).thenReturn(Optional.of(owner));
        when(reservationListRepository.findByStoreId(storeId)).thenReturn(
                List.of(reservation1, reservation2));

        // when
        List<ReservationList> reservations = ownerService.getReservationsByStoreId(username,
                storeId);

        // then
        verify(ownerRepository, times(1)).findByUserUsername(username);
        verify(reservationListRepository, times(1)).findByStoreId(storeId);
        assertEquals(2, reservations.size());
        assertEquals(1L, reservations.get(0).getId());
        assertEquals(2L, reservations.get(1).getId());
    }

    @Test
    @DisplayName("가게 ID로 예약 조회 예외 - 오너없음")
    public void testGetReservationsByStoreId_UserNotFound() {
        // given
        String username = "username";
        Long storeId = 1L;
        when(ownerRepository.findByUserUsername(username)).thenReturn(Optional.empty());

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> ownerService.getReservationsByStoreId(username, storeId));
        assertEquals(NOT_EXIST_USER.getMessage(), exception.getMessage());

        verify(ownerRepository, times(1)).findByUserUsername(username);
        verifyNoMoreInteractions(reservationListRepository);
    }

    @Test
    @DisplayName("예약 수락 성공")
    public void testAcceptReservation_Successful() {
        // given
        String username = "username";

        Long storeId = 1L;
        Store existingStore = Store.builder()
                .storeName("storeName")
                .build();
        existingStore.setId(storeId);

        Long reservationId = 1L;
        ReservationList reservation = ReservationList.builder()
                .store(existingStore)
                .permission(false)
                .expiredTime(LocalDateTime.now().plusHours(1)) //예약만료까지 1시간 여유있음
                .build();
        reservation.setId(reservationId);
        Owner owner = new Owner();
        owner.add(existingStore);

        when(reservationListRepository.findById(reservationId)).thenReturn(
                Optional.of(reservation));
        when(ownerRepository.findByUserUsername(username)).thenReturn(Optional.of(owner));

        // when
        ReservationList result = ownerService.acceptReservation(username, reservationId);

        // then
        verify(reservationListRepository, times(1)).findById(reservationId);
        verify(ownerRepository, times(1)).findByUserUsername(username);
        assertTrue(result.isPermission());
    }

    @Test
    @DisplayName("예약수락 예외 - 유효하지않은 예약")
    public void testAcceptReservation_ReservationNotFound() {
        // Arrange
        String username = "username";
        Long reservationId = 1L;
        when(reservationListRepository.findById(reservationId)).thenReturn(Optional.empty());

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> ownerService.acceptReservation(username, reservationId));
        assertEquals(NOT_VALID_RESERVATION.getMessage(), exception.getMessage());

        verify(reservationListRepository, times(1)).findById(reservationId);
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    @DisplayName("예약수락 예외 - 자신의 가게 아님")
    public void testAcceptReservation_UserNotFound() {
        // given
        String username = "username";

        Store existingStore = Store.builder()
                .storeName("storeName")
                .build();

        Long reservationId = 123L;
        ReservationList reservation = ReservationList.builder()
                .store(existingStore)
                .permission(false)
                .expiredTime(LocalDateTime.now().plusHours(1)) //예약만료까지 1시간 여유있음
                .build();
        reservation.setId(reservationId);

        Owner owner = new Owner();

        when(reservationListRepository.findById(reservationId)).thenReturn(
                Optional.of(reservation));
        when(ownerRepository.findByUserUsername(username)).thenReturn(Optional.of(owner));

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> ownerService.acceptReservation(username, reservationId));
        assertEquals(NOT_EXIST_STORE.getMessage(), exception.getMessage());

        verify(reservationListRepository, times(1)).findById(reservationId);
        verify(ownerRepository, times(1)).findByUserUsername(username);
    }

    @Test
    @DisplayName("예약수락 예외 - 이미 수락된 예약")
    public void testAcceptReservation_AlreadyAccepted() {
        // given
        String username = "username";

        Long storeId = 1L;
        Store existingStore = Store.builder()
                .storeName("storeName")
                .build();
        existingStore.setId(storeId);

        Long reservationId = 1L;
        ReservationList reservation = ReservationList.builder()
                .store(existingStore)
                .permission(true)
                .expiredTime(LocalDateTime.now().plusHours(1)) //예약만료까지 1시간 여유있음
                .build();
        reservation.setId(reservationId);

        Owner owner = new Owner();
        owner.add(existingStore);

        when(reservationListRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class, () -> ownerService.acceptReservation(username, reservationId));
        assertEquals(ALREADY_ACCEPTED_RESERVATION.getMessage(), exception.getMessage());

        verify(reservationListRepository, times(1)).findById(reservationId);
        verifyNoMoreInteractions(ownerRepository);
    }

    @Test
    @DisplayName("예약수락 예외 - 예약 만료시간이 지났을 때")
    public void testAcceptReservation_ExpiredReservation() {
        // given
        String username = "username";
        Long reservationId = 123L;
        Owner owner = new Owner();
        owner.setId(1L);
        Store store =  Store.builder()
                .storeName("storeName")
                .build();
        owner.add(store);

        ReservationList reservation = ReservationList.builder()
                .store(store)
                .expiredTime(LocalDateTime.now().minusMinutes(1)) //만료시간 지남
                .build();
        reservation.setId(reservationId);

        when(reservationListRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // Act and Assert
        CustomException exception = assertThrows(CustomException.class, () -> ownerService.acceptReservation(username, reservationId));
        assertEquals(ALREADY_EXPIRED_RESERVATION.getMessage(), exception.getMessage());

        verify(reservationListRepository, times(1)).findById(reservationId);
        verifyNoMoreInteractions(ownerRepository);
    }


}
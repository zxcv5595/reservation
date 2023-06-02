package com.zxcv5595.reservation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Review;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.CreateReservation;
import com.zxcv5595.reservation.dto.ReviewDto;
import com.zxcv5595.reservation.repository.ReservationListRepository;
import com.zxcv5595.reservation.repository.ReviewRepository;
import com.zxcv5595.reservation.repository.StoreRepository;
import com.zxcv5595.reservation.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class ConsumerServiceTest {

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private ReservationListRepository reservationListRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ConsumerService consumerService;

    @Test
    @DisplayName("키워드로 가게 검색")
    public void searchStoresNameByKeyword_ShouldReturnMatchingStores() {
        // given
        String keyword = "keyword";
        Pageable page = PageRequest.of(0, 10);
        Store store1 = Store.builder()
                .storeName("Store 1")
                .build();
        store1.setId(1L);
        Store store2 = Store.builder()
                .storeName("Store 2")
                .build();
        store2.setId(2L);
        List<Store> stores = Arrays.asList(
                store1, store2
        );
        Page<Store> expectedPage = new PageImpl<>(stores, page, stores.size());

        given(storeRepository.findByStoreNameContainingIgnoreCase(keyword, page))
                .willReturn(expectedPage);

        // when
        Page<Store> result = consumerService.searchStoresNameByKeyword(keyword, page);

        // then
        assertEquals(result, expectedPage);
    }

    @Test
    @DisplayName("예약하기 - 2시간 전에는 미리 해야함")
    public void createReservation_ShouldCreateReservationSuccessfully() {
        // given
        String username = "testUser";
        CreateReservation.Request request = CreateReservation.Request.builder()
                .storeName("My Store")
                .reservationDate(LocalDate.now())
                .reservationTime(LocalTime.now().plusHours(3)) // 3시간 전 미리 예약하는 시나리오
                .build();

        User user = User.builder()
                .username(username)
                .build();
        Store store = Store.builder()
                .storeName("My Store")
                .build();
        store.setId(1L);

        ReservationList newReservation = ReservationList.builder()
                .user(user)
                .store(store)
                .reservationTime(LocalDateTime.of(request.getReservationDate(),
                        request.getReservationTime()))
                .permission(false)
                .visited(false)
                .build();
        newReservation.setId(1L);

        ArgumentCaptor<ReservationList> captor = ArgumentCaptor.forClass(ReservationList.class);

        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        given(storeRepository.findByStoreName(request.getStoreName())).willReturn(
                Optional.of(store));
        given(reservationListRepository.save(captor.capture())).willReturn(null);

        // when
        ReservationList result = consumerService.createReservation(username, request);

        // then
        assertEquals(result, captor.getValue());
        assertEquals("My Store", captor.getValue().getStore().getStoreName());
    }

    @Test
    @DisplayName("리뷰 작성")
    public void writeReview_ShouldWriteReviewSuccessfully() {
        // given
        User user = User.builder()
                .username("testUser")
                .build();
        Store store = Store.builder()
                .storeName("My Store")
                .build();
        store.setId(1L);
        ReviewDto.Request request = new ReviewDto.Request(1L, "Great store", 5);
        ReservationList reservation =ReservationList.builder()
                .user(user)
                .store(store)
                .permission(true)
                .visited(true)
                .reservationTime(LocalDateTime.now())
                .build();
        reservation.setId(1L);

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);

        given(reservationListRepository.findById(request.getReservationId())).willReturn(Optional.of(reservation));
        given(reviewRepository.existsByReservationId(request.getReservationId())).willReturn(false);
        given(reviewRepository.save(captor.capture())).willReturn(null);

        // when
        Review result = consumerService.writeReview(user, request);

        // then
        assertEquals(result,captor.getValue());
        assertEquals("Great store",captor.getValue().getContent());
    }

}

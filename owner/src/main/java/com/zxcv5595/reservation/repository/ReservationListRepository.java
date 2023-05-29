package com.zxcv5595.reservation.repository;

import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationListRepository extends JpaRepository<ReservationList, Long> {

    List<ReservationList> findByReservationTimeAndStoreId(LocalDateTime reservationTime,
            Long storeId);

    List<ReservationList> findByStoreId(Long storeId);

    Optional<ReservationList> findById(Long reservationId);

    Optional<ReservationList> findByUserPhoneAndStoreIdAndReservationTimeBetween(String phone,
            Long storeId, LocalDateTime start, LocalDateTime end);

    boolean existsByUserAndStoreAndReservationTimeBetween(User user, Store store,
            LocalDateTime startTime, LocalDateTime endTime);


}

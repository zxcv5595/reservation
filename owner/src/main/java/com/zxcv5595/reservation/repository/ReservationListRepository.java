package com.zxcv5595.reservation.repository;

import com.zxcv5595.reservation.domain.ReservationList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationListRepository extends JpaRepository<ReservationList, Long> {
    List<ReservationList> findByReservationTimeAndStoreId(LocalDateTime reservationTime, Long storeId);
    List<ReservationList> findByStoreId(Long storeId);

    Optional<ReservationList> findById(Long reservationId);

}

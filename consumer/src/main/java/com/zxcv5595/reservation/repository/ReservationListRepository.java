package com.zxcv5595.reservation.repository;

import com.zxcv5595.reservation.domain.ReservationList;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationListRepository extends JpaRepository<ReservationList, Long> {
    List<ReservationList> findByReservationTime(LocalDateTime reservationTime);

}

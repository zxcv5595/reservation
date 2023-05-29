package com.zxcv5595.reservation.repository;

import com.zxcv5595.reservation.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review,Long> {
        boolean existsByReservationId(Long reservationId);
}

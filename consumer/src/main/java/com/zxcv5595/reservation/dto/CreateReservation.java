package com.zxcv5595.reservation.dto;

import com.zxcv5595.reservation.domain.ReservationList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

public class CreateReservation {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        private String storeName;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate reservationDate;
        @DateTimeFormat(pattern = "HH:mm")
        private LocalTime reservationTime;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private String username;
        private String storeName;
        private LocalDateTime reservationTime;
        private String permission;

        public static Response fromEntity(ReservationList reservationList) {
            return Response.builder()
                    .storeName(reservationList.getStore().getStoreName())
                    .username(reservationList.getUser().getUsername())
                    .reservationTime(reservationList.getReservationTime())
                    .permission("승인이 확인되면 예약이 완료됩니다.")
                    .build();
        }
    }


}

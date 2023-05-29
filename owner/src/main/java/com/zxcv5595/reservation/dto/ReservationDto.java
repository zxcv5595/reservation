package com.zxcv5595.reservation.dto;

import com.zxcv5595.reservation.domain.ReservationList;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {

    private String storeName;

    private String username;

    private LocalDateTime reservationTime;

    private boolean permission;
    private boolean visited;


    public static ReservationDto from(ReservationList list){
        return ReservationDto.builder()
                .storeName(list.getStore().getStoreName())
                .username(list.getUser().getUsername())
                .reservationTime(list.getReservationTime())
                .permission(list.isPermission())
                .visited(list.isVisited())
                .build();
    }
}

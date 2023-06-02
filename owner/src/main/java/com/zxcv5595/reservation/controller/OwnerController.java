package com.zxcv5595.reservation.controller;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.AddStore;
import com.zxcv5595.reservation.dto.Register.Response;
import com.zxcv5595.reservation.dto.ReservationDto;
import com.zxcv5595.reservation.service.OwnerService;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping("/register")
    public ResponseEntity<Response> register(
            @AuthenticationPrincipal User currentUser) { //로그인 후 토큰 필요

        ownerService.register(currentUser.getUsername());

        return ResponseEntity.ok(new Response("등록이 완료되었습니다."));

    }

    @PostMapping("/add-store")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<AddStore.Response> addStore(@AuthenticationPrincipal User currentUser,
            @Valid @RequestBody AddStore.Request request) {

        Owner owner = ownerService.addStore(currentUser.getUsername(), request); //가게 추가

        return ResponseEntity.ok(AddStore.Response.from(owner));
    }

    @GetMapping("/read-reservation")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<ReservationDto>> getReservationByStoreId(
            @AuthenticationPrincipal User currentUser,
            @RequestParam Long storeId) {

        String username = currentUser.getUsername();

        List<ReservationList> reservations = ownerService.getReservationsByStoreId(username,
                storeId);// 예약리스트 조회

        List<ReservationDto> result = reservations.stream().map(ReservationDto::from)
                .collect(Collectors.toList()); //dto 변환

        return ResponseEntity.ok(result);
    }

    @PostMapping("/accept-reservation")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ReservationDto> acceptReservation( //예약 수락
            @AuthenticationPrincipal User crrentUser, @RequestParam Long reservationId) {

        ReservationList reservation = ownerService.acceptReservation(crrentUser.getUsername(),
                reservationId); //수락
        return ResponseEntity.ok(ReservationDto.from(reservation));
    }

}

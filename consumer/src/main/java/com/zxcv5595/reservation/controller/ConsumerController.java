package com.zxcv5595.reservation.controller;

import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.CreateReservation;
import com.zxcv5595.reservation.dto.StoreDto;
import com.zxcv5595.reservation.service.ConsumerService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    private final ConsumerService consumerService;

    @GetMapping("/search-store")
    public ResponseEntity<Page<StoreDto>> searchStoresNameByKeyword(@RequestParam String keyword,
            Pageable page) {
        Page<Store> stores = consumerService.searchStoresNameByKeyword(keyword,
                page); //키와 페이징설정을 받아 가게 검색
        Page<StoreDto> storeDto = stores.map(StoreDto::from); // dto로 변환
        return ResponseEntity.ok(storeDto);
    }

    @PostMapping("/add-reservation")
    public ResponseEntity<CreateReservation.Response> addReservation(
            @AuthenticationPrincipal User currentUser, @Valid @RequestBody
    CreateReservation.Request request) {

        ReservationList reservation = consumerService.createReservation(currentUser.getUsername(),
                request); //예약

        return ResponseEntity.ok(CreateReservation.Response.fromEntity(reservation));
    }
}


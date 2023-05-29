package com.zxcv5595.reservation.controller;

import com.zxcv5595.reservation.service.KioskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kiosk")
public class KioskController {

    private final KioskService kioskService;

    @PostMapping("/visit")
    public ResponseEntity<String> visitedReservation(@RequestParam String phone) {
        kioskService.visitedReservation(phone);

        return ResponseEntity.ok("방문처리 되었습니다.");
    }

}

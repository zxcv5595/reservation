package com.zxcv5595.reservation.controller;

import com.zxcv5595.reservation.dto.VisitedReservation;
import com.zxcv5595.reservation.dto.VisitedReservation.Response;
import com.zxcv5595.reservation.service.KioskService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kiosk")
public class KioskController {

    private final KioskService kioskService;

    @PostMapping("/visit")
    public ResponseEntity<Response> visitedReservation(
            @Valid @RequestBody VisitedReservation.Request request) {
        kioskService.visitedReservation(request);

        return ResponseEntity.ok(new Response("방문처리 되었습니다."));
    }

}

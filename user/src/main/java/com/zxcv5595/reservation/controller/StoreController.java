package com.zxcv5595.reservation.controller;

import com.zxcv5595.reservation.dto.Store.SignUpStore;
import com.zxcv5595.reservation.dto.Store.SignUpStore.Response;
import com.zxcv5595.reservation.service.SignUpService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final SignUpService signUpService;

    @PostMapping("/sign")
    public ResponseEntity<Response> signUpStore(@Valid @RequestBody SignUpStore.Request req) {
        return ResponseEntity.ok(signUpService.signUpStore(req));
    }

}

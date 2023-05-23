package com.zxcv5595.reservation.controller;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.AddStore;
import com.zxcv5595.reservation.service.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/owner")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@AuthenticationPrincipal User currentUser) { //로그인 토큰 필요

        ownerService.register(currentUser.getUsername());
        return ResponseEntity.ok("등록이 완료되었습니다.");

    }

    @PostMapping("/add-store")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<AddStore.Response> addStore(@AuthenticationPrincipal User currentUser,
           @RequestBody AddStore.Request request) {

        Owner owner = ownerService.addStore(currentUser.getUsername(), request); //가게 추가

        return ResponseEntity.ok(AddStore.Response.from(owner));
    }
}

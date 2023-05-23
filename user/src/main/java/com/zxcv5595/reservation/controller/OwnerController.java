package com.zxcv5595.reservation.controller;

import static com.zxcv5595.reservation.type.UserType.ROLE_OWNER;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.dto.Store.AddStore;
import com.zxcv5595.reservation.dto.Store.SignInOwner;
import com.zxcv5595.reservation.dto.Store.SignUpOwner;
import com.zxcv5595.reservation.dto.Store.SignUpOwner.Response;
import com.zxcv5595.reservation.security.TokenProvider;
import com.zxcv5595.reservation.service.OwnerService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerController {

    private static final String TOKEN_PREFIX = "Bearer ";
    private final OwnerService ownerService;
    private final TokenProvider tokenProvider;


    @PostMapping("/signup")
    public ResponseEntity<String> signUpOwner(@Valid @RequestBody SignUpOwner.Request req) {
        Response owner = ownerService.signUpOwner(req);
        String ownerName = owner.getUsername();
        return ResponseEntity.ok(ownerName + "님 가입을 축하합니다!!");
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signInOwner(@RequestBody SignInOwner.Request req) {
        Owner owner = ownerService.signInOwner(req);
        String token = tokenProvider.generateToken(owner.getUsername(), ROLE_OWNER);
        return ResponseEntity.ok(token);

    }

    @PostMapping("/add-store")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<AddStore.Response>  addStore(@RequestHeader("Authorization") String token,
           @RequestBody AddStore.Request req) {
        token = token.substring(TOKEN_PREFIX.length());
        String username = tokenProvider.getUsername(token);
        //가게 추가
        Owner owner = ownerService.addStore(username, req);

        return ResponseEntity.ok(AddStore.Response.from(owner));
    }


}

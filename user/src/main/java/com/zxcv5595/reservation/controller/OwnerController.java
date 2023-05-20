package com.zxcv5595.reservation.controller;

import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_USER;
import static com.zxcv5595.reservation.type.UserType.ROLE_OWNER;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.dto.Store.SignInOwner;
import com.zxcv5595.reservation.dto.Store.SignUpOwner;
import com.zxcv5595.reservation.dto.Store.SignUpOwner.Response;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.OwnerRepository;
import com.zxcv5595.reservation.security.TokenProvider;
import com.zxcv5595.reservation.service.OwnerService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerController {

    private final OwnerService ownerService;
    private final TokenProvider tokenProvider;

    private final OwnerRepository ownerRepository;

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

    @GetMapping("/info")
    @PreAuthorize("hasRole('OWNER')")
    public Owner getInfo(@RequestHeader(value = "Authorization") String header,
            @RequestParam("username") String userName) {
        return ownerRepository.findByUsername(userName).orElseThrow(
                () -> new CustomException(NOT_EXIST_USER)
        );
    }

}

package com.zxcv5595.reservation.controller;

import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.SignIn;
import com.zxcv5595.reservation.dto.SignIn.Request;
import com.zxcv5595.reservation.dto.SignUp;
import com.zxcv5595.reservation.dto.SignUp.Response;
import com.zxcv5595.reservation.security.TokenProvider;
import com.zxcv5595.reservation.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<Response> signup(@Valid @RequestBody SignUp.Request request) {
        /*
        회원가입
        username, password(min = 8, max = 20),
        phone(010-1234-1234)
         */
        Response newUser = userService.signup(request); //회원 정보 저장
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/signin")
    public ResponseEntity<SignIn.Response> signin(@RequestBody Request request) {

        User user = userService.signin(request); //유저 인증

        String token = tokenProvider.generateToken(user.getUsername(), user.getRoles());//토큰 생성

        return ResponseEntity.ok(new SignIn.Response(token));
    }
}

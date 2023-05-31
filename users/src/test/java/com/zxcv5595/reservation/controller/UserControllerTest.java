package com.zxcv5595.reservation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.SignIn;
import com.zxcv5595.reservation.dto.SignUp;
import com.zxcv5595.reservation.security.TokenProvider;
import com.zxcv5595.reservation.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private UserController userController;


    @Test
    @DisplayName("회원가입 성공")
    public void testSignup_Successful() {
        // given
        SignUp.Request signupRequest = new SignUp.Request("username", "password", "010-1234-1234");
        User savedUser = User.builder()
                .username("username")
                .password("encodedPassword")
                .build();

        when(userService.signup(any(SignUp.Request.class))).thenReturn(
                SignUp.Response.fromEntity(savedUser));

        // when
        ResponseEntity<String> response = userController.signup(signupRequest);

        // then
        verify(userService, times(1)).signup(signupRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("username님 환영합니다.", response.getBody());
    }

    @Test
    @DisplayName("로그인 성공")
    public void testSignin_Successful() {
        // given
        SignIn.Request signinRequest = new SignIn.Request("username", "password");
        User authenticatedUser = User.builder()
                .username("username")
                .password("encodedPassword")
                .build();
        String generatedToken = "generatedToken";

        when(userService.signin(any(SignIn.Request.class))).thenReturn(authenticatedUser);
        when(tokenProvider.generateToken(anyString(), any())).thenReturn(generatedToken);

        // when
        ResponseEntity<String> response = userController.signin(signinRequest);

        // Assert
        verify(userService, times(1)).signin(signinRequest);
        verify(tokenProvider, times(1)).generateToken("username", authenticatedUser.getRoles());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(generatedToken, response.getBody());
    }
}

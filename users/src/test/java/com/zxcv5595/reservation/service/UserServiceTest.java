package com.zxcv5595.reservation.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.SignIn;
import com.zxcv5595.reservation.dto.SignUp;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;


    @Test
    @DisplayName("회원가입 성공")
    public void testSignup_Successful() {
        // given
        SignUp.Request signupRequest = new SignUp.Request("username", "password", "010-1234-1234");
        User savedUser = User.builder()
                .username("username")
                .password("encodedPassword")
                .phone("010-1234-1234")
                .build();

        when(userRepository.existsByUsername(anyString())).thenReturn(false); //중복된 유저 없음
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword"); // 인코딩된 비밀번호
        when(userRepository.save(any(User.class))).thenReturn(savedUser); // 저장된 유저 정보

        // when
        userService.signup(signupRequest);

        // then
        verify(userRepository, times(1)).existsByUsername("username");
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    @DisplayName("회원가입 예외 - 이미 가입된 유저")
    public void testSignup_UserAlreadyExists() {
        // given
        SignUp.Request signupRequest = new SignUp.Request("username", "password", "010-1234-1234");

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        // Act and Assert
        assertThrows(CustomException.class, () -> userService.signup(signupRequest));

        verify(userRepository, times(1)).existsByUsername("username");
        verifyNoMoreInteractions(passwordEncoder, userRepository); //  mock 객체들과 상호작용이 더 이상 없음
    }

    @Test
    @DisplayName("로그인 성공")
    public void testSignin_Successful() {
        // given
        SignIn.Request signinRequest = new SignIn.Request("username", "password");
        User existingUser = User.builder()
                .username("username")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // when
        userService.signin(signinRequest);

        // then
        verify(userRepository, times(1)).findByUsername("username");
        verify(passwordEncoder, times(1)).matches("password", "encodedPassword");

    }

    @Test
    @DisplayName("로인인 예외 - 해당하는 유저없음")
    public void testSignin_UserNotFound() {
        // given
        SignIn.Request signinRequest = new SignIn.Request("username", "password");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(CustomException.class, () -> userService.signin(signinRequest));

        verify(userRepository, times(1)).findByUsername("username");
        verifyNoMoreInteractions(passwordEncoder, userRepository); //  mock 객체들과 상호작용이 더 이상 없음
    }

    @Test
    @DisplayName("로그인 예외 - 비밀번호 매칭 실패")
    public void testSignin_IncorrectPassword() {
        // Arrange
        SignIn.Request signinRequest = new SignIn.Request("username", "password");
        User existingUser = User.builder()
                .username("username")
                .password("encodedPassword")
                .build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Act and Assert
        assertThrows(CustomException.class, () -> userService.signin(signinRequest));

        verify(userRepository, times(1)).findByUsername("username");
        verify(passwordEncoder, times(1)).matches("password", "encodedPassword");
        verifyNoMoreInteractions(userRepository);

    }
}



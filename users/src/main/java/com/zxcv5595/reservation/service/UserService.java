package com.zxcv5595.reservation.service;

import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_EXIST_USER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_USER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_MATCHED_PASSWORD;

import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.SignIn;
import com.zxcv5595.reservation.dto.SignUp;
import com.zxcv5595.reservation.dto.SignUp.Response;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));
    }

    //회원가입
    public SignUp.Response signup(SignUp.Request user) {
        // 유저 존재하는지 확인
        boolean exists = userRepository.existsByUsername(user.getUsername());

        if (exists) { // 존재하면 예외처리: 이미 가입된 회원
            throw new CustomException(ALREADY_EXIST_USER);
        }

        //비밀번호 암호화 후, 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User saved = userRepository.save(user.toEntity());

        // 반환형식에 맞춰 리턴
        return Response.fromEntity(saved);
    }

    //로그인
    public User signin(SignIn.Request user) {
        User getUser = userRepository.findByUsername(user.getUsername())
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER)); //유저 확인

        if (!passwordEncoder.matches(user.getPassword(), getUser.getPassword())) {
            throw new CustomException(NOT_MATCHED_PASSWORD); // 비밀번호 확인
        }

        return getUser;
    }

}

package com.zxcv5595.reservation.service;

import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.dto.Store.SignUpStore;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.StoreRepository;
import com.zxcv5595.reservation.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

@Service
@RequiredArgsConstructor
@RequestMapping("/signup")
public class SignUpService {

    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    public SignUpStore.Response signUpStore(SignUpStore.Request member) {
        // 유저 존재하는지 확인
        boolean exists = storeRepository.existsByUsername(member.getUsername());

        if (exists) { // 존재하면 예외처리: 이미 가입된 회원
            throw new CustomException(ErrorCode.ALREADY_EXIST_USER);
        }

        //비밀번호 암호화 후, 저장
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        Store saved = storeRepository.save(member.toEntity());

        // 반환형식에 맞춰 리턴
        return SignUpStore.Response.fromEntity(saved);

    }

}

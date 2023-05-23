package com.zxcv5595.reservation.service;

import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_EXIST_STORE;
import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_EXIST_USER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_USER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_MATCHED_PASSWORD;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.dto.Store.AddStore;
import com.zxcv5595.reservation.dto.Store.SignInOwner;
import com.zxcv5595.reservation.dto.Store.SignUpOwner;
import com.zxcv5595.reservation.dto.Store.SignUpOwner.Response;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.OwnerRepository;
import com.zxcv5595.reservation.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService implements UserDetailsService {

    private final OwnerRepository ownerRepository;
    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return ownerRepository.findByUsername(username).orElseThrow(
                () -> new CustomException(NOT_EXIST_USER));
    }

    public Owner signInOwner(SignInOwner.Request user) { //로그인
        // 유저 조회 후, 없으면 예외처리
        Owner owner = ownerRepository.findByUsername(user.getUsername()).orElseThrow(
                () -> new CustomException(NOT_EXIST_USER)
        );

        //비밀번호 확인 후, 틀리면 예외처리
        if (!passwordEncoder.matches(user.getPassword(), owner.getPassword())) {
            throw new CustomException(NOT_MATCHED_PASSWORD);
        }

        return owner;
    }

    public SignUpOwner.Response signUpOwner(SignUpOwner.Request user) { //회원가입
        // 유저 존재하는지 확인
        boolean exists = ownerRepository.existsByUsername(user.getUsername());

        if (exists) { // 존재하면 예외처리: 이미 가입된 회원
            throw new CustomException(ALREADY_EXIST_USER);
        }

        //비밀번호 암호화 후, 저장
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Owner saved = ownerRepository.save(user.toEntity());
        // 반환형식에 맞춰 리턴
        return Response.fromEntity(saved);

    }

    /*
    가게 등록
    1.가게명
    2.주소
    3.설명
    토큰을 통해 사용자 확인 후, 가게 등록
     */
    @Transactional
    public Owner addStore(String username, AddStore.Request req) {
        //유저 불러오기
        Owner owner = ownerRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER));

        // 추가할 가게
        Store newStore = req.fromEntity();

        // Owner를 설정해줍니다.
        newStore.setOwner(owner);

        //가게 등록 가능여부 체크 - 중복된 이름
        validateAddStore(owner, newStore);

        //유저의 가게에 새로운 가게 추가
        owner.getStores().add(newStore);

        //가게 저장
        storeRepository.save(newStore);

        return owner;

    }

    private void validateAddStore(Owner owner, Store newStore) {
        //가게 등록 가능여부 체크 - 중복된 이름
        if (owner.getStores().stream()
                .anyMatch(store -> store.getStoreName().equals(newStore.getStoreName()))) {
            throw new CustomException(ALREADY_EXIST_STORE);
        }
    }
}

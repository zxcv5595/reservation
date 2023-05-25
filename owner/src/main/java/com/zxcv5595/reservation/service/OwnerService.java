package com.zxcv5595.reservation.service;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.AddStore;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.OwnerRepository;
import com.zxcv5595.reservation.repository.UserRepository;
import com.zxcv5595.reservation.type.ErrorCode;
import com.zxcv5595.reservation.type.Role;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;


    public void register(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER)); // 유저 확인

        if (ownerRepository.existsByUser(user)) {
            throw new CustomException(ErrorCode.ALREADY_REGISTER_OWNER); // 이미 OWNER에 등록되어있는지 확인
        }

        getAuthority(user); //ROLE_OWNER 권한부여

        ownerRepository.save(Owner.from(user)); // owner에 등록

    }

    @Transactional
    public Owner addStore(String username, AddStore.Request request) {
        Owner owner = ownerRepository.findByUserUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER)); // 등록된 오너 가져오기

        Store newStore = Store.builder() //등록할 가게
                .storeName(request.getStoreName())
                .address(request.getAddress())
                .description(request.getDescription())
                .build();

        validateStore(owner, newStore); //가게 이름 중복 확인

        owner.add(newStore); //가게 추가

        return owner;
    }

    private void getAuthority(User user) {
        Set<Role> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(Role.ROLE_OWNER);
        user.setRoles(roles);
    }

    private void validateStore(Owner owner, Store newStore) {
        if (owner.getStores().stream()
                .anyMatch(store -> store.getStoreName().equals(newStore.getStoreName()))) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_STORE);
        }
    }
}

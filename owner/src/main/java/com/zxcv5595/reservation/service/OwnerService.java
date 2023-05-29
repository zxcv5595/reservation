package com.zxcv5595.reservation.service;

import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_ACCEPTED_RESERVATION;
import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_EXIST_STORE;
import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_EXPIRED_RESERVATION;
import static com.zxcv5595.reservation.type.ErrorCode.ALREADY_REGISTER_OWNER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_STORE;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_EXIST_USER;
import static com.zxcv5595.reservation.type.ErrorCode.NOT_VALID_RESERVATION;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.AddStore;
import com.zxcv5595.reservation.exception.CustomException;
import com.zxcv5595.reservation.repository.OwnerRepository;
import com.zxcv5595.reservation.repository.ReservationListRepository;
import com.zxcv5595.reservation.repository.UserRepository;
import com.zxcv5595.reservation.type.Role;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final UserRepository userRepository;
    private final ReservationListRepository reservationListRepository;


    public void register(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER)); // 유저 확인

        if (ownerRepository.existsByUser(user)) {
            throw new CustomException(ALREADY_REGISTER_OWNER); // 이미 OWNER에 등록되어있는지 확인
        }

        getAuthority(user); //ROLE_OWNER 권한부여

        ownerRepository.save(Owner.from(user)); // owner에 등록

    }

    @Transactional
    public Owner addStore(String username, AddStore.Request request) {
        Owner owner = ownerRepository.findByUserUsername(username)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER)); // 등록된 오너 가져오기

        Store newStore = Store.builder() //등록할 가게
                .storeName(request.getStoreName())
                .address(request.getAddress())
                .description(request.getDescription())
                .build();

        validateStoreName(owner, newStore); //가게 이름 중복 확인

        owner.add(newStore); //가게 추가

        return owner;
    }

    public List<ReservationList> getReservationsByStoreId(String username, Long storeId) { //예약조회
        Owner owner = ownerRepository.findByUserUsername(username)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER)); //오너 정보

        //자신의 가게인지 체크
        validateStoreId(storeId, owner);

        return reservationListRepository.findByStoreId(storeId);
    }

    @Transactional
    public ReservationList acceptReservation(String username, Long reservationId) {
        ReservationList reservation = reservationListRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(NOT_VALID_RESERVATION)); //수락할 예약

        checkAccept(reservation); //수락 전, 유효성 검사

        Owner owner = ownerRepository.findByUserUsername(username)
                .orElseThrow(() -> new CustomException(NOT_EXIST_USER)); //오너 정보
        Long storeId = reservation.getStore().getId(); //가게 아이디

        //자신의 가게인지 체크
        validateStoreId(storeId, owner);

        reservation.setPermission(true); //수락

        return reservation;
    }

    private void checkAccept(ReservationList reservation) {
        if (reservation.isPermission()) { //이미 수락된 예약인지 확인
            throw new CustomException(ALREADY_ACCEPTED_RESERVATION);
        }
        if (reservation.getExpiredTime().isBefore(LocalDateTime.now())) { //만료기간 확인
            throw new CustomException(ALREADY_EXPIRED_RESERVATION);
        }
    }

    private void validateStoreId(Long storeId, Owner owner) {
        boolean valid = owner.getStores().stream()
                .anyMatch(store -> Objects.equals(store.getId(), storeId));
        if (!valid) {
            throw new CustomException(NOT_EXIST_STORE);
        }
    }

    private void getAuthority(User user) {
        Set<Role> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(Role.ROLE_OWNER);
        user.setRoles(roles);
    }

    private void validateStoreName(Owner owner, Store newStore) {
        if (owner.getStores().stream()
                .anyMatch(store -> store.getStoreName().equals(newStore.getStoreName()))) {
            throw new CustomException(ALREADY_EXIST_STORE);
        }
    }
}

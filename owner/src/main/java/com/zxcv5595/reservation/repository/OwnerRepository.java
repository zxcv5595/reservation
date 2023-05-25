package com.zxcv5595.reservation.repository;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnerRepository extends JpaRepository<Owner, Long>  {

    boolean existsByUser(User user);

    @EntityGraph("userWithStores")
    Optional<Owner> findByUserUsername(String username);
}

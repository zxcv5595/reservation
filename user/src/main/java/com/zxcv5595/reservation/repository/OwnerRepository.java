package com.zxcv5595.reservation.repository;

import com.zxcv5595.reservation.domain.Owner;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    boolean existsByUsername(String userName);
    @EntityGraph("userWithStores")
    Optional<Owner> findByUsername(String userName);
}

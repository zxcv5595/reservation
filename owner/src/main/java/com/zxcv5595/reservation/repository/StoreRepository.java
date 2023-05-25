package com.zxcv5595.reservation.repository;

import com.zxcv5595.reservation.domain.Store;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Page<Store> findByStoreNameContainingIgnoreCase(String keyword, Pageable p);
    Optional<Store> findByStoreName(String storeName);
}

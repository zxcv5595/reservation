package com.zxcv5595.reservation.dto;

import com.zxcv5595.reservation.domain.Store;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {
    private String storeName;
    private String address;
    private String description;

    public static StoreDto from(Store store) {
        return StoreDto.builder()
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .description(store.getDescription())
                .build();
    }

    @Override // OwnerController add-store 테스트코드에 사용됨
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StoreDto storeDto = (StoreDto) o;
        return Objects.equals(storeName, storeDto.storeName) &&
                Objects.equals(address, storeDto.address) &&
                Objects.equals(description, storeDto.description);
    }

}
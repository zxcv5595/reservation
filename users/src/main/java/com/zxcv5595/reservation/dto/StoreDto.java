package com.zxcv5595.reservation.dto.Store;

import com.zxcv5595.reservation.domain.Store;
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

    public static StoreDto from(Store store){
        return StoreDto.builder()
                .storeName(store.getStoreName())
                .address(store.getAddress())
                .description(store.getDescription())
                .build();
    }

}

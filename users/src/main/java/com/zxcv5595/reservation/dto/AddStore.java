package com.zxcv5595.reservation.dto.Store;

import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.domain.Store;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AddStore {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        private String storeName;
        private String address;
        private String description;

        public Store fromEntity() {
            return Store.builder()
                    .storeName(this.storeName)
                    .address(this.address)
                    .description(this.description)
                    .build();
        }

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private String username;
        private List<StoreDto> stores;

        public static Response from(Owner owner) {
            List<StoreDto> storeDTOs = owner.getStores().stream()
                    .map(StoreDto::from)
                    .collect(Collectors.toList());

            return Response.builder()
                    .username(owner.getUsername())
                    .stores(storeDTOs)
                    .build();
        }

    }


}

package com.zxcv5595.reservation.dto;

import com.zxcv5595.reservation.domain.Owner;
import java.util.ArrayList;
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
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private List<StoreDto> stores = new ArrayList<>();

        public static Response from(Owner owner) {
            return Response.builder()
                    .stores(owner.getStores().stream()
                            .map(StoreDto::from).collect(
                            Collectors.toList()))
                    .build();
        }
    }

}

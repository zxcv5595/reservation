package com.zxcv5595.reservation.dto;

import com.zxcv5595.reservation.domain.Owner;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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

        @NotBlank(message = "Store name must not be blank")
        @Size(max = 100, message = "Store name cannot exceed 100 characters")
        private String storeName;

        @NotBlank(message = "Address must not be blank")
        private String address;

        @Size(max = 500, message = "Description cannot exceed 500 characters")
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

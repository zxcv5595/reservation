package com.zxcv5595.reservation.dto.Store;

import com.zxcv5595.reservation.domain.Store;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SignUpStore {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @NotEmpty(message = "Username cannot be empty")
        private String username;

        @NotEmpty(message = "Password cannot be empty")
        @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
        private String password;

        @NotEmpty(message = "Store name cannot be empty")
        private String storeName;

        @NotEmpty(message = "Address cannot be empty")
        private String address;

        @NotEmpty(message = "Description cannot be empty")
        private String description;

        public Store toEntity() {
            return Store.builder()
                    .username(this.username)
                    .password(this.password)
                    .storeName(this.storeName)
                    .address(this.address)
                    .description(this.description)
                    .build();
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private String username;
        private String storeName;
        private String address;
        private String description;

        public static Response fromEntity(Store store) {
            return Response.builder()
                    .username(store.getUsername())
                    .storeName(store.getStoreName())
                    .address(store.getAddress())
                    .description(store.getDescription())
                    .build();
        }

    }

}

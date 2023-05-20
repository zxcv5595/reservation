package com.zxcv5595.reservation.dto.Store;

import com.zxcv5595.reservation.domain.Owner;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SignUpOwner {

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


        public Owner toEntity() {
            return Owner.builder()
                    .username(this.username)
                    .password(this.password)
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

        public static Response fromEntity(Owner owner) {
            return Response.builder()
                    .username(owner.getUsername())
                    .build();
        }

    }

}

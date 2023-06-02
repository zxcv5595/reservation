package com.zxcv5595.reservation.dto;

import com.zxcv5595.reservation.domain.User;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class SignUp {

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

        @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "Invalid phone number ex:010-1234-1234")
        private String phone;


        public User toEntity() {
            return User.builder()
                    .username(this.username)
                    .password(this.password)
                    .phone(this.phone)
                    .build();
        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private String message;

        public static Response fromEntity(User user) {
            return Response.builder()
                    .message(user.getUsername()+"님 환영합니다.")
                    .build();
        }

    }

}

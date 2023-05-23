package com.zxcv5595.reservation.dto;

import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.type.Role;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

        @Enumerated(EnumType.STRING)
        private Role role;

        public User toEntity() {
            Set<Role> newRole = new HashSet<>(Collections.singletonList(role));
            return User.builder()
                    .username(this.username)
                    .password(this.password)
                    .phone(this.phone)
                    .roles(newRole)
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

        public static Response fromEntity(User user) {
            return Response.builder()
                    .username(user.getUsername())
                    .build();
        }

    }

}
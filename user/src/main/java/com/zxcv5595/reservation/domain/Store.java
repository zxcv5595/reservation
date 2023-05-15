package com.zxcv5595.reservation.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
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
@Entity
public class Store extends BaseEntity {
    //아이디, 비밀번호, 매장 명, 상점위치, 상점 설명

    private String username;
    @JsonIgnore
    private String password;

    private String storeName;
    private String address;
    private String description;

}

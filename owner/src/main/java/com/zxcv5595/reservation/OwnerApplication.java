package com.zxcv5595.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
@Profile("owner")
public class OwnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.zxcv5595.reservation.OwnerApplication.class, args);
    }
}
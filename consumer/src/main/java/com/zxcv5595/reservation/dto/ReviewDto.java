package com.zxcv5595.reservation.dto;

import com.zxcv5595.reservation.domain.Review;
import javax.persistence.Column;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReviewDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {

        private Long reservationId;

        @Column(nullable = false)
        @Size(max = 500,message = "500자를 초과할 수 없습니다.")
        private String content;

        @Column(nullable = false)
        @Min(value = 0, message = "0 이상이어야 합니다.")
        @Max(value = 5, message = "5 이하여야 합니다.")
        private int rating;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {

        private Long reservationId;
        private String content;

        private int rating;

        public static Response fromEntity(Review review){
            return Response.builder()
                    .reservationId(review.getReservation().getId())
                    .content(review.getContent())
                    .rating(review.getRating())
                    .build();
        }
    }
}

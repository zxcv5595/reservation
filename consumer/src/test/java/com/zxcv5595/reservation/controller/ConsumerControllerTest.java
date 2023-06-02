package com.zxcv5595.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxcv5595.reservation.EnableMockMvc;
import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Review;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.CreateReservation;
import com.zxcv5595.reservation.dto.ReviewDto;
import com.zxcv5595.reservation.security.TokenProvider;
import com.zxcv5595.reservation.service.ConsumerService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@AutoConfigureRestDocs(outputDir = "target/snippets", uriScheme = "https", uriHost = "docs.api.com")
@WebMvcTest(ConsumerController.class)
@ActiveProfiles("consumer")
@ExtendWith({RestDocumentationExtension.class})
@EnableMockMvc
class ConsumerControllerTest {

    @MockBean
    private static UserDetailsService userDetailsService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ConsumerService consumerService;

    @MockBean
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("키워드로 가게 검색")
    void searchStoresNameByKeyword_ShouldReturnListOfStores() throws Exception {
        // given
        String keyword = "Store";
        Pageable pageable = PageRequest.of(0, 10); // example pageable

        Store store1 = Store.builder()
                .storeName("Store 1")
                .address("address")
                .description("description")
                .build();
        Store store2 = Store.builder()
                .storeName("Store 2")
                .address("address")
                .description("description")
                .build();
        List<Store> stores = Arrays.asList(store1, store2);
        Page<Store> storePage = new PageImpl<>(stores, pageable, stores.size());

        // 사용자 인증 및 권한 설정
        User currentUser = User.builder()
                .username("testUser")
                .password("password")
                .roles(new HashSet<>())
                .build();

        // 모의 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null));

        given(consumerService.searchStoresNameByKeyword(keyword, pageable)).willReturn(storePage);

        // when
        mockMvc.perform(get("/consumer/search-store")
                        .param("keyword", keyword)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                // Verify other fields of the response
                .andDo(document("consumer-search-stores",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                parameterWithName("keyword").description(
                                        "The keyword for store search"),
                                parameterWithName("page").description("The page number"),
                                parameterWithName("size").description("The page size")
                        ),
                        responseFields(
                                fieldWithPath("content[].storeName").description(
                                        "The name of the store"),
                                fieldWithPath("content[].address").description(
                                        "The address of the store"),
                                fieldWithPath("content[].description").description(
                                        "The description of the store"),
                                fieldWithPath("pageable").description("Paging information"),
                                fieldWithPath("pageable.sort").description("Sorting information"),
                                fieldWithPath("pageable.sort.sorted").description(
                                        "Is the sort order sorted"),
                                fieldWithPath("pageable.sort.unsorted").description(
                                        "Is the sort order unsorted"),
                                fieldWithPath("pageable.sort.empty").description(
                                        "Is the sort order empty"),
                                fieldWithPath("pageable.pageNumber").description("The page number"),
                                fieldWithPath("pageable.pageSize").description("The page size"),
                                fieldWithPath("pageable.offset").description("The offset"),
                                fieldWithPath("pageable.paged").description(
                                        "Is it a paged request"),
                                fieldWithPath("pageable.unpaged").description(
                                        "Is it an unpaged request"),
                                fieldWithPath("sort").description("Sorting information"),
                                fieldWithPath("sort.sorted").description(
                                        "Is the sort order sorted"),
                                fieldWithPath("sort.unsorted").description(
                                        "Is the sort order unsorted"),
                                fieldWithPath("sort.empty").description("Is the sort order empty"),
                                fieldWithPath("last").description("Is it the last page"),
                                fieldWithPath("totalPages").description("Total number of pages"),
                                fieldWithPath("totalElements").description(
                                        "Total number of elements"),
                                fieldWithPath("number").description("The current page number"),
                                fieldWithPath("first").description("Is it the first page"),
                                fieldWithPath("numberOfElements").description(
                                        "The number of elements in the current page"),
                                fieldWithPath("size").description("The page size"),
                                fieldWithPath("empty").description("Is the page empty")
                        )
                ));

    }

    @Test
    @DisplayName("예약 하기")
    void addReservation_ShouldReturnReservation() throws Exception {
        // given
        User currentUser = User.builder()
                .username("testUser")
                .password("password")
                .roles(new HashSet<>())
                .build();

        Store store = Store.builder()
                .storeName("StoreName")
                .address("address")
                .description("description")
                .build();
        store.setId(1L);

        CreateReservation.Request request = CreateReservation.Request.builder()
                .reservationDate(LocalDate.now())
                .reservationTime(LocalTime.now())
                .storeName("StoreName")
                .build();
        // Set the request data

        ReservationList reservation = ReservationList.builder()
                .user(currentUser)
                .store(store)
                .reservationTime(LocalDateTime.now().plusHours(3))
                .permission(false)
                .visited(false)
                .build();
        reservation.setId(1L);

        given(consumerService.createReservation(anyString(), any()))
                .willReturn(reservation);

        // 사용자 인증 및 권한 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null));

        // when
        mockMvc.perform(post("/consumer/add-reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("consumer-add-reservation",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("storeName").description(
                                        "가게 이름"),
                                fieldWithPath("reservationDate").description(
                                        "날짜 (pattern = yyyy-MM-dd)"),
                                fieldWithPath("reservationTime").description(
                                        "시간 (pattern = HH:mm")
                        ),
                        responseFields(
                                fieldWithPath("username").description(
                                        "예약자"),
                                fieldWithPath("storeName").description(
                                        "가게이름"),
                                fieldWithPath("reservationTime").description(
                                        "예약날짜,시간"),
                                fieldWithPath("permission").description(
                                        "승인이 확인되면 예약이 완료됩니다.")
                        )
                ));
    }

    @Test
    @DisplayName("리뷰 쓰기")
    void writeReview_ShouldReturnReview() throws Exception {
        // given
        User currentUser = User.builder()
                .username("testUser")
                .password("password")
                .roles(new HashSet<>())
                .build();

        Store store = Store.builder()
                .storeName("StoreName")
                .address("address")
                .description("description")
                .build();
        store.setId(1L);

        ReservationList reservation = ReservationList.builder()
                .user(currentUser)
                .store(store)
                .reservationTime(LocalDateTime.now().plusHours(3))
                .permission(false)
                .visited(false)
                .build();
        reservation.setId(1L);

        ReviewDto.Request request = ReviewDto.Request.builder()
                .reservationId(1L)
                .content("Great store")
                .rating(5)
                .build();

        Review review = Review.builder()
                .reservation(reservation)
                .content("Great store")
                .rating(5)
                .build();

        given(consumerService.writeReview(any(), any()))
                .willReturn(review);

        // 사용자 인증 및 권한 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null));

        // when
        mockMvc.perform(post("/consumer/write-review")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                // Verify other fields of the response
                .andDo(document("consumer-write-review",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("reservationId").description("예약 아이디번호"),
                                fieldWithPath("content").description("리뷰내용"),
                                fieldWithPath("rating").description("별점 0~5(int)")
                        ),
                        responseFields(
                                fieldWithPath("reservationId").description("예약 아이디번호"),
                                fieldWithPath("content").description("리뷰내용"),
                                fieldWithPath("rating").description("별점 0~5(int)")
                        )
                ));
    }


}
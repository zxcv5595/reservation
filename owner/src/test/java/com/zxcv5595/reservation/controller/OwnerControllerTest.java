package com.zxcv5595.reservation.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxcv5595.reservation.EnableMockMvc;
import com.zxcv5595.reservation.domain.Owner;
import com.zxcv5595.reservation.domain.ReservationList;
import com.zxcv5595.reservation.domain.Store;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.AddStore.Request;
import com.zxcv5595.reservation.security.TokenProvider;
import com.zxcv5595.reservation.service.OwnerService;
import com.zxcv5595.reservation.type.Role;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@AutoConfigureRestDocs(outputDir = "target/snippets", uriScheme = "https", uriHost = "docs.api.com")
@WebMvcTest(OwnerController.class)
@ActiveProfiles("owner")
@ExtendWith({RestDocumentationExtension.class})
@EnableMockMvc
class OwnerControllerTest {

    @MockBean
    private static UserDetailsService userDetailsService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OwnerService ownerService;

    @MockBean
    private TokenProvider tokenProvider;


    @Test
    @DisplayName("오너등록")
    void register_ShouldReturnOkResponse() throws Exception {
        // given
        User currentUser = User.builder()
                .username("testUser")
                .password("password")
                .roles(new HashSet<>())
                .build();

        // 모의 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null));

        given(userDetailsService.loadUserByUsername("testUser"))
                .willReturn(currentUser);

        // when
        doNothing().when(ownerService).register(eq("testUser"));

        mockMvc.perform(post("/owner/register")
                        .with(user(userDetailsService.loadUserByUsername("testUser")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("등록이 완료되었습니다."))
                .andDo(document("owner-register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("message").description("등록이 완료되었습니다.")
                        )
                ));
        // then
        verify(ownerService).register(eq("testUser"));

    }

    @Test
    @DisplayName("가게 추가")
    void addStore_ShouldReturnOkResponse() throws Exception {
        // given
        User currentUser = User.builder()
                .username("testUser")
                .password("password")
                .roles(Collections.singleton(Role.ROLE_OWNER))
                .build();

        Request request = Request.builder()
                .storeName("My Store")
                .address("address")
                .description("description")
                .build();

        // 모의 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null));

        Owner owner = Owner.builder()
                .user(currentUser)
                .stores(new ArrayList<>())
                .build();
        Store store = Store.builder()
                .storeName("My Store")
                .address("address")
                .description("description")
                .build();
        store.setId(123L);
        owner.add(store);

        given(userDetailsService.loadUserByUsername("testUser"))
                .willReturn(currentUser);

        // captor 생성
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);

        given(ownerService.addStore(anyString(), captor.capture())).willReturn(owner);

        // when
        mockMvc.perform(post("/owner/add-store")
                        .with(user(userDetailsService.loadUserByUsername("testUser")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                // Verify other fields of the response
                .andDo(document("owner-add-store",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                // Define the request fields
                                fieldWithPath("storeName").description("The name of the store"),
                                fieldWithPath("address").description("The address of the store"),
                                fieldWithPath("description").description(
                                        "The description of the store")
                        ),
                        responseFields(
                                // Define other response fields
                                fieldWithPath("stores[].storeName").description(
                                        "The name of the store"),
                                fieldWithPath("stores[].address").description(
                                        "The address of the store"),
                                fieldWithPath("stores[].description").description(
                                        "The description of the store")
                        )
                ));

        // then
        verify(ownerService).addStore(eq("testUser"), captor.capture());
        assertEquals("My Store", captor.getValue().getStoreName());
    }

    @Test
    @DisplayName("가게별 예약 조회")
    void getReservationByStoreId_ShouldReturnOkResponse() throws Exception {
        // given
        User currentUser = User.builder()
                .username("testUser")
                .password("password")
                .roles(Collections.singleton(Role.ROLE_OWNER))
                .build();
        Store store = Store.builder()
                .storeName("My Store")
                .address("address")
                .description("description")
                .build();
        Long storeId = 123L;
        store.setId(storeId);

        // 모의 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null));

        List<ReservationList> reservations = new ArrayList<>();
        ReservationList reservation1 = ReservationList.builder()
                .store(store)
                .reservationTime(LocalDateTime.now())
                .user(currentUser)
                .build();
        reservation1.setId(1L);
        reservations.add(reservation1);

        ReservationList reservation2 = ReservationList.builder()
                .store(store)
                .reservationTime(LocalDateTime.now().plusMinutes(30))
                .user(currentUser)
                .build();
        reservation2.setId(2L);
        reservations.add(reservation2);

        given(userDetailsService.loadUserByUsername("testUser"))
                .willReturn(currentUser);

        given(ownerService.getReservationsByStoreId("testUser", storeId))
                .willReturn(reservations);

        // when
        mockMvc.perform(get("/owner/read-reservation")
                        .with(user(userDetailsService.loadUserByUsername("testUser")))
                        .param("storeId", String.valueOf(storeId)))
                .andExpect(status().isOk())
                // Verify other fields of the response
                .andDo(document("owner-get-reservation-by-store-id",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                // Define the request parameters
                                parameterWithName("storeId").description("The ID of the store")
                        ),
                        responseFields(
                                // Define other response fields
                                fieldWithPath("[].storeName").description("The ID of the reservation"),
                                fieldWithPath("[].username").description("The name of the reservation"),
                                fieldWithPath("[].reservationTime").description("The ID of the reservation"),
                                fieldWithPath("[].permission").description("The ID of the reservation"),
                                fieldWithPath("[].visited").description("The ID of the reservation")
                        )
                ));

        // then
        verify(ownerService).getReservationsByStoreId("testUser", storeId);
    }

    @Test
    @DisplayName("가게별 예약 수락")
    void acceptReservation_ShouldReturnOkResponse() throws Exception {
        // given
        User currentUser = User.builder()
                .username("testUser")
                .password("password")
                .roles(Collections.singleton(Role.ROLE_OWNER))
                .build();
        Store store = Store.builder()
                .storeName("My Store")
                .address("address")
                .description("description")
                .build();
        Long storeId = 123L;
        store.setId(storeId);
        Long reservationId = 123L;

        User customer = User.builder()
                .username("customer")
                .build();

        // 모의 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null));

        ReservationList reservation = ReservationList.builder()
                .user(customer)
                .store(store)
                .reservationTime(LocalDateTime.now())
                .permission(true)
                .visited(false)
                .build();

        given(userDetailsService.loadUserByUsername("testUser"))
                .willReturn(currentUser);

        given(ownerService.acceptReservation(eq("testUser"), eq(reservationId)))
                .willReturn(reservation);

        // when
        mockMvc.perform(post("/owner/accept-reservation")
                        .with(user(userDetailsService.loadUserByUsername("testUser")))
                        .param("reservationId", String.valueOf(reservationId)))
                .andExpect(status().isOk())
                // Verify other fields of the response
                .andDo(document("owner-accept-reservation",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestParameters(
                                // Define the request parameters
                                parameterWithName("reservationId").description("The ID of the reservation")
                        ),
                        responseFields(
                                // Define other response fields
                                fieldWithPath("storeName").description("The ID of the reservation"),
                                fieldWithPath("username").description("The name of the reservation"),
                                fieldWithPath("reservationTime").description("The ID of the reservation"),
                                fieldWithPath("permission").description("The ID of the reservation"),
                                fieldWithPath("visited").description("The ID of the reservation")
                        )
                ));

        // then
        verify(ownerService).acceptReservation(eq("testUser"), eq(reservationId));
    }



}
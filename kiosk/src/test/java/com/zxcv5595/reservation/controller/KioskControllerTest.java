package com.zxcv5595.reservation.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxcv5595.reservation.EnableMockMvc;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.VisitedReservation;
import com.zxcv5595.reservation.security.TokenProvider;
import com.zxcv5595.reservation.service.KioskService;
import java.util.HashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@WebMvcTest(KioskController.class)
@ActiveProfiles("kiosk")
@ExtendWith({RestDocumentationExtension.class})
@EnableMockMvc
class KioskControllerTest {

    @MockBean
    private static UserDetailsService userDetailsService;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private KioskService kioskService;

    @MockBean
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("방문 처리")
    void visitedReservation_Success() throws Exception {
        // given

        VisitedReservation.Request request = VisitedReservation.Request.builder()
                .phone("010-1234-1234")
                .storeId(1L)
                .build();

        // 사용자 인증 및 권한 설정
        User currentUser = User.builder()
                .username("testUser")
                .password("password")
                .roles(new HashSet<>())
                .build();

        // 모의 사용자 설정
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(currentUser, null));

        // when
        mockMvc.perform(post("/kiosk/visit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("방문처리 되었습니다."))
                .andDo(document("kiosk/visited-reservation",
                        requestFields(
                                fieldWithPath("phone").description(
                                        "사용자 전화번호(pattern : 010-1234-1234)"),
                                fieldWithPath("storeId").description("가게 ID")
                        ),
                        responseFields(
                                fieldWithPath("message").description("응답 메시지 : 방문처리 되었습니다.")
                        )
                ));
    }
}
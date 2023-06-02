package com.zxcv5595.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxcv5595.reservation.EnableMockMvc;
import com.zxcv5595.reservation.domain.User;
import com.zxcv5595.reservation.dto.SignIn;
import com.zxcv5595.reservation.dto.SignUp;
import com.zxcv5595.reservation.dto.SignUp.Request;
import com.zxcv5595.reservation.security.TokenProvider;
import com.zxcv5595.reservation.service.UserService;
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
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureRestDocs(outputDir = "target/snippets", uriScheme = "https", uriHost = "docs.api.com")
@WebMvcTest(controllers = UserController.class)
@ExtendWith(RestDocumentationExtension.class)
@EnableMockMvc
public class UserControllerTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @MockBean
    private TokenProvider tokenProvider;


    @Test
    @DisplayName("회원가입 성공")
    public void testSignup_Successful() throws Exception {
        SignUp.Request signupRequest = new SignUp.Request("username", "password", "010-1234-1234");
        User savedUser = User.builder()
                .username("username")
                .password("encodedPassword")
                .phone("010-1234-1234")
                .build();

        // ArgumentCaptor를 생성하여 signup 메서드의 인자를 캡처합니다.
        ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(SignUp.Request.class);
        when(userService.signup(captor.capture())).thenReturn(
                SignUp.Response.fromEntity(savedUser));

        mockMvc.perform(post("/user/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("username님 환영합니다."))
                .andDo(document("signup",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("username").description("The username"),
                                fieldWithPath("password").description("The password"),
                                fieldWithPath("phone").description("The phone number")
                        ),
                        responseFields(
                                fieldWithPath("message").description("The welcome message")
                        )
                ));

        // ArgumentCaptor에서 캡처한 객체를 가져와서 verify 메서드에 전달합니다.
        verify(userService, times(1)).signup(captor.getValue());
    }

    @Test
    @DisplayName("로그인 성공")
    public void testSignin_Successful() throws Exception {
        SignIn.Request signinRequest = new SignIn.Request("username", "password");
        User user = User.builder()
                .username("username")
                .password("encodedPassword")
                .build();

        ArgumentCaptor<SignIn.Request> captor = ArgumentCaptor.forClass(SignIn.Request.class);
        when(userService.signin(captor.capture())).thenReturn(user);
        when(tokenProvider.generateToken(eq("username"), any())).thenReturn("generatedToken");

        mockMvc.perform(post("/user/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signinRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("generatedToken"))
                .andDo(document("signin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("username").description("The username"),
                                fieldWithPath("password").description("The password")
                        ),
                        responseFields(
                                fieldWithPath("token").description("The generated token")
                        )
                ));

        verify(userService, times(1)).signin(captor.getValue());
        verify(tokenProvider, times(1)).generateToken(eq("username"), any());
    }


}


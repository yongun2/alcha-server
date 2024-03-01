package pcrc.alcha.ui.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import pcrc.alcha.exception.MessageType;

import java.util.LinkedHashMap;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Value("${local.img.default}")
    private String DEFAULT_IMG_URL;
    private final String BASE_URI = "/api/v1/users";

    @Test
    @Transactional
    void signUp() throws Exception {
        // given
        UserCreateRequest request_200 = UserCreateRequest.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImageBase64(null)
                .build();

        UserCreateRequest request_400_missing_required = UserCreateRequest.builder()
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImageBase64(null)
                .build();

        UserCreateRequest request_400_username_pattern_capital = UserCreateRequest.builder()
                .username("Qsd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImageBase64(null)
                .build();

        UserCreateRequest request_400_username_pattern_short = UserCreateRequest.builder()
                .username("a")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImageBase64(null)
                .build();
        // when
        ResultActions perform_200 = mvc.perform(
                post(BASE_URI + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request_200))
        );

        ResultActions perform_400_missing_required = mvc.perform(
                post(BASE_URI + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request_400_missing_required))
        );

        ResultActions perform_400_username_pattern_capital = mvc.perform(
                post(BASE_URI + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request_400_username_pattern_capital))
        );

        ResultActions perform_400_username_pattern_short = mvc.perform(
                post(BASE_URI + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request_400_username_pattern_short))
        );

        // then
        perform_200.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username", is(request_200.username)))
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.nickname", is(request_200.nickname)))
                .andExpect(jsonPath("$.data.profileImgUrl", is(DEFAULT_IMG_URL)));

        perform_400_missing_required.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_REQUEST.getMessage())));

        perform_400_username_pattern_capital.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_USERNAME_PATTERN.getMessage())));

        perform_400_username_pattern_short.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.BAD_USERNAME_PATTERN.getMessage())));
    }

    @Test
    void login() {
    }

    @Test
    @Transactional
    @DisplayName("아이디, 닉네임 중복 확인 테스트")
    void checkDuplicate() throws Exception {
        // given
        UserCreateRequest request_200 = UserCreateRequest.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImageBase64(null)
                .build();

        ResultActions perform_200 = mvc.perform(
                post(BASE_URI + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request_200))
        );

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("username", "test");
        params.add("nickname", "hello");

        // when
        ResultActions perform_400_no_params = mvc.perform(
                get(BASE_URI + "/check-duplicate")
        );
        ResultActions perform_400_include_both_param = mvc.perform(
                get(BASE_URI + "/check-duplicate")
                        .params(params)
        );
        ResultActions perform_409_username = mvc.perform(
                get(BASE_URI + "/check-duplicate")
                        .param("username", request_200.username)
        );

        ResultActions perform_409_nickname = mvc.perform(
                get(BASE_URI + "/check-duplicate")
                        .param("nickname", request_200.nickname)
        );

        // then
        perform_409_username.andExpect(status().isConflict());
        perform_409_nickname.andExpect(status().isConflict());
        perform_400_no_params.andExpect(status().isBadRequest());
        perform_400_include_both_param.andExpect(status().isBadRequest());
    }

    @Test
    void logout() {
    }

    @Test
    void healthTest() throws Exception {
        // given

        // when

        // then
        mvc.perform(get(BASE_URI + "/health-check"))
                .andExpect(status().isOk());
    }

    @Builder
    private record UserCreateRequest(String username, String password, String nickname, String profileImageBase64) {

    }
}
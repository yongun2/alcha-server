package pcrc.alcha.ui.controller;

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
import pcrc.alcha.application.domain.jwt.Token;
import pcrc.alcha.application.service.user.UserOperationUseCase;
import pcrc.alcha.application.service.user.UserOperationUseCase.UserCreateCommand;
import pcrc.alcha.application.utils.JWTTokenUtils;
import pcrc.alcha.exception.MessageType;
import pcrc.alcha.infrastructure.persistance.entity.UserEntity;
import pcrc.alcha.infrastructure.persistance.repository.auth.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pcrc.alcha.application.service.user.UserOperationUseCase.UserLoginCommand;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserOperationUseCase operationUseCase;

    @Autowired
    private UserRepository repository;

    @Autowired
    private JWTTokenUtils jwtTokenUtils;

    @Value("${local.img.default}")
    private String DEFAULT_IMG_URL;
    private final String BASE_URI = "/api/v1/users";

    @Test
    @Transactional
    @DisplayName("회원가입 테스트")
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
                post(BASE_URI + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request_200))
        );

        ResultActions perform_400_missing_required = mvc.perform(
                post(BASE_URI + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request_400_missing_required))
        );

        ResultActions perform_400_username_pattern_capital = mvc.perform(
                post(BASE_URI + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request_400_username_pattern_capital))
        );

        ResultActions perform_400_username_pattern_short = mvc.perform(
                post(BASE_URI + "/register")
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
    @Transactional
    @DisplayName("로그아웃 테스트")
    void logout() throws Exception {
        // given
        UserCreateCommand command = UserCreateCommand.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();

        UserLoginCommand loginCommand = UserLoginCommand.builder()
                .username(command.username())
                .password(command.password())
                .build();
        operationUseCase.register(command);
        operationUseCase.login(loginCommand);

        Token token = jwtTokenUtils.generateToken(command.nickname());
        // when
        ResultActions perform_200 = mvc.perform(delete(BASE_URI + "/logout")
                .header("Authorization", token.accessToken())
        );
        ResultActions perform_403 = mvc.perform(delete(BASE_URI + "/logout"));
        // then
        perform_200.andExpect(status().isOk());
        perform_403.andExpect(status().isForbidden());

        Optional<UserEntity> optional = repository.findUserEntityByUsername(command.username());
        assertThat(optional.isPresent()).isTrue();
        assertThat(optional.get().getRefreshTokenEntity()).isNull();
    }

    @Test
    @Transactional
    @DisplayName("로그인 테스트")
    void login() throws Exception {
        // given
        UserCreateCommand command = UserCreateCommand.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();
        operationUseCase.register(command);

        UserLoginRequest request_200 = UserLoginRequest.builder()
                .username("asd1234")
                .password("qwer1234!")
                .build();
        UserLoginRequest request_404 = UserLoginRequest.builder()
                .username("asd1234")
                .password("d")
                .build();
        // when
        ResultActions perform_200 = mvc.perform(
                post(BASE_URI + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request_200))
        );
        ResultActions perform_404 = mvc.perform(
                post(BASE_URI + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request_404))
        );
        // then
        Optional<UserEntity> userEntityByNickname = repository.findUserEntityByNickname(command.nickname());
        assertThat(userEntityByNickname.isPresent()).isTrue();
        perform_200.andExpect(status().isOk());
        perform_200.andExpect(jsonPath("$.data.refreshTokenId", is(userEntityByNickname.get().getRefreshTokenEntity().getId())));

        perform_404.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errors[0].errorMessage", is(MessageType.USER_NOT_FOUND.getMessage())));
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
                post(BASE_URI + "/register")
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
    void healthTest() throws Exception {
        // given
        Token token = jwtTokenUtils.generateToken("testUserA");
        // when

        // then
        mvc.perform(get(BASE_URI + "/health-check")
                        .header("Authorization", token.accessToken())
                )
                .andExpect(status().isOk());
    }

    @Builder
    private record UserCreateRequest(String username, String password, String nickname, String profileImageBase64) {

    }

    @Builder
    private record UserLoginRequest(String username, String password) {

    }
}
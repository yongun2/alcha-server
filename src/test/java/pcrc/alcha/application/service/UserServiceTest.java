package pcrc.alcha.application.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pcrc.alcha.application.domain.auth.User;
import pcrc.alcha.application.service.user.UserService;
import pcrc.alcha.exception.AlchaException;
import pcrc.alcha.exception.MessageType;
import pcrc.alcha.infrastructure.persistance.entity.UserEntity;
import pcrc.alcha.infrastructure.persistance.repository.auth.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static pcrc.alcha.application.service.user.UserOperationUseCase.*;
import static pcrc.alcha.application.service.user.UserReadUseCase.*;
import static pcrc.alcha.application.service.user.UserReadUseCase.UserFindQuery.*;

@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService service;
    @Autowired
    private UserRepository repository;

    @Value("${local.img.default}")
    private String DEFAULT_IMG_URL;

    @Test
    @Transactional
    @DisplayName("유저 저장 테스트")
    void register() {
        // given
        UserCreateCommand command_200 = UserCreateCommand.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();
        UserCreateCommand command_200_kor_nickname = UserCreateCommand.builder()
                .username("asd1235")
                .password("qwer1234!")
                .nickname("안녕하세요")
                .profileImgBase64(null)
                .build();

        UserCreateCommand command_400_username_short = UserCreateCommand.builder()
                .username("a")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();
        UserCreateCommand command_400_username_long = UserCreateCommand.builder()
                .username("afjdsklfjsdlkfjsdlkfjlksdfjklsdjflksdjflksdj")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();
        UserCreateCommand command_400_username_invalid_character_capital = UserCreateCommand.builder()
                .username("Dj")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();
        UserCreateCommand command_400_username_invalid_character = UserCreateCommand.builder()
                .username("jf !!@#!")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();

        UserCreateCommand command_400_password_short = UserCreateCommand.builder()
                .username("asd1234")
                .password("a")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();

        UserCreateCommand command_400_password_long = UserCreateCommand.builder()
                .username("asd1234")
                .password("afsdjfjdslkfjdslkfjdslkfjlkdsjflksdjflksdjflkdsjfl")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();

        UserCreateCommand command_400_nickname_short = UserCreateCommand.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("d")
                .profileImgBase64(null)
                .build();

        UserCreateCommand command_400_nickname_long = UserCreateCommand.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("jdsklfjklsdfjlkdsjflksdjfldsjflkdsjflksdjflkdjsfkjsdlkfjdslk")
                .profileImgBase64(null)
                .build();



        // when
        FindUserResult register_200 = service.register(command_200);
        FindUserResult register_200_nickname = service.register(command_200_kor_nickname);

        Optional<UserEntity> command_result = repository.findUserEntityByUsername(command_200.username());
        Optional<UserEntity> command_result_nickname = repository.findUserEntityByUsername(command_200_kor_nickname.username());
        // then
        assertThat(register_200.username()).isEqualTo(command_200.username());
        assertThat(register_200.profileImgUrl()).isEqualTo(DEFAULT_IMG_URL);
        assertThat(command_result.isPresent()).isTrue();

        assertThat(register_200_nickname.username()).isEqualTo(command_200_kor_nickname.username());
        assertThat(register_200_nickname.profileImgUrl()).isEqualTo(DEFAULT_IMG_URL);
        assertThat(command_result_nickname.isPresent()).isTrue();

        assertThatThrownBy(() -> service.register(command_400_username_short))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.BAD_USERNAME_PATTERN.getMessage());

        assertThatThrownBy(() -> service.register(command_400_username_long))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.BAD_USERNAME_PATTERN.getMessage());

        assertThatThrownBy(() -> service.register(command_400_username_invalid_character_capital))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.BAD_USERNAME_PATTERN.getMessage());

        assertThatThrownBy(() -> service.register(command_400_username_invalid_character))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.BAD_USERNAME_PATTERN.getMessage());

        assertThatThrownBy(() -> service.register(command_400_password_short))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.BAD_PASSWORD_PATTERN.getMessage());
        assertThatThrownBy(() -> service.register(command_400_password_long))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.BAD_PASSWORD_PATTERN.getMessage());

        assertThatThrownBy(() -> service.register(command_400_nickname_short))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.BAD_NICKNAME_PATTERN.getMessage());
        assertThatThrownBy(() -> service.register(command_400_nickname_long))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.BAD_NICKNAME_PATTERN.getMessage());

    }

    @Test
    @Transactional
    @DisplayName("로그인 테스트")
    void login() {
        // given
        UserCreateCommand default_command = UserCreateCommand.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();

        UserLoginCommand command_200 = UserLoginCommand.builder()
                .username(default_command.username())
                .password(default_command.password())
                .build();

        UserLoginCommand command_404_username = UserLoginCommand.builder()
                .username("")
                .password(default_command.password())
                .build();
        UserLoginCommand command_404_password = UserLoginCommand.builder()
                .username(default_command.username())
                .password("")
                .build();
        service.register(default_command);
        FindLoginResult result_200 = service.login(command_200);
        // when

        Optional<UserEntity> userEntityByUsernameOptional = repository.findUserEntityByUsername(default_command.username());
        // then
        assertThat(userEntityByUsernameOptional.isPresent()).isTrue();
        assertThat(userEntityByUsernameOptional.get().getRefreshTokenEntity().getId()).isEqualTo(result_200.refreshTokenId());

        assertThatThrownBy(() -> service.login(command_404_username))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.USER_NOT_FOUND.getMessage());
        assertThatThrownBy(() -> service.login(command_404_password))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.USER_NOT_FOUND.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("로그아웃 테스트")
    void logout() {
        // given
        UserCreateCommand default_command = UserCreateCommand.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgBase64(null)
                .build();

        UserLoginCommand command_200 = UserLoginCommand.builder()
                .username(default_command.username())
                .password(default_command.password())
                .build();
        service.register(default_command);
        service.login(command_200);

        UserFindQuery query_200 = builder()
                .nickname(default_command.nickname())
                .build();
        UserFindQuery query_404 = builder()
                .nickname("")
                .build();
        // when
        service.logout(query_200);

        // then
        Optional<UserEntity> userEntityByNicknameOptional = repository.findUserEntityByNickname(default_command.nickname());
        assertThat(userEntityByNicknameOptional.isPresent()).isTrue();
        assertThat(userEntityByNicknameOptional.get().getRefreshTokenEntity()).isNull();

        assertThatThrownBy(() -> service.logout(query_404))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.USER_NOT_FOUND.getMessage());
    }


    @Test
    @Transactional
    @DisplayName("아이디로 유저 찾기 테스트")
    void findByUsername() {
        // given
        User defaultUser = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgUrl(null)
                .build();

        repository.save(new UserEntity(defaultUser));

        UserFindQuery query_200 = builder()
                .username(defaultUser.getUsername())
                .build();

        UserFindQuery query_404 = builder()
                .username("nothing")
                .build();
        // when
        FindUserResult query_200_result = service.getUserByUsername(query_200);
        // then
        assertThat(query_200_result.username()).isEqualTo(query_200.username());
        assertThatThrownBy(() -> service.getUserByUsername(query_404))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.USER_NOT_FOUND.getMessage());
    }

    @Test
    @Transactional
    @DisplayName("닉네임으로 유저 찾기 테스트")
    void findByNickname() {
        // given
        User defaultUser = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgUrl(null)
                .build();

        repository.save(new UserEntity(defaultUser));

        UserFindQuery query_200 = builder()
                .nickname(defaultUser.getNickname())
                .build();

        UserFindQuery query_404 = builder()
                .nickname("nothing")
                .build();
        // when
        FindUserResult query_200_result = service.getUserByNickname(query_200);
        // then
        assertThat(query_200_result.nickname()).isEqualTo(query_200.nickname());
        assertThatThrownBy(() -> service.getUserByNickname(query_404))
                .isInstanceOf(AlchaException.class)
                .hasMessage(MessageType.USER_NOT_FOUND.getMessage());
    }

}
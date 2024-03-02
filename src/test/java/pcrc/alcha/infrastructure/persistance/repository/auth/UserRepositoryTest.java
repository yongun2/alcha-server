package pcrc.alcha.infrastructure.persistance.repository.auth;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.annotation.Transactional;
import pcrc.alcha.application.domain.auth.RefreshToken;
import pcrc.alcha.application.domain.auth.User;
import pcrc.alcha.infrastructure.persistance.entity.UserEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

//    @AfterEach
//    void deleteUsers() {
//        repository.deleteAll();
//    }

    @Test
    @DisplayName("유저 저장 테스트")
    @Transactional
    void register() {
        // given
        User success_testUserA = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgUrl("dd")
                .build();
        User fail_username_duplicate = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserB")
                .profileImgUrl("dd")
                .build();
        User fail_nickname_duplicate = User.builder()
                .username("asd1235")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgUrl("dd")
                .build();

        // when
        UserEntity save = repository.save(new UserEntity(success_testUserA));
        // then
        assertThat(save.getNickname()).isEqualTo(success_testUserA.getNickname());
        assertThat(save.getRefreshTokenEntity()).isNull();

        assertThatThrownBy(() -> repository.save(new UserEntity(fail_username_duplicate)))
                .isInstanceOf(DataIntegrityViolationException.class);

        assertThatThrownBy(() -> repository.save(new UserEntity(fail_nickname_duplicate)))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    @DisplayName("유저 저장 실패 - 아이디 중복")
    @Transactional
    void register_fail_duplicate_username() {
        // given
        User success_testUserA = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgUrl("dd")
                .build();

        // when
        UserEntity save = repository.save(new UserEntity(success_testUserA));
        // then
        assertThat(save.getNickname()).isEqualTo(success_testUserA.getNickname());
        assertThat(save.getRefreshTokenEntity()).isNull();

    }
    @Test
    @DisplayName("유저 저장 실패 - 닉네임 중복")
    @Transactional
    void register_fail_duplicate_nickname() {
        // given
        User success_testUserA = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgUrl("dd")
                .build();
        User fail_nickname_duplicate = User.builder()
                .username("asd1235")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgUrl("dd")
                .build();

        // when
        UserEntity save = repository.save(new UserEntity(success_testUserA));
        // then
        assertThat(save.getNickname()).isEqualTo(success_testUserA.getNickname());
        assertThat(save.getRefreshTokenEntity()).isNull();
        assertThatThrownBy(() -> repository.save(new UserEntity(fail_nickname_duplicate)))
                .isInstanceOf(DataIntegrityViolationException.class);

    }
    @Test
    @DisplayName("유저 리프레시토큰 저장 테스트")
    @Transactional
    void saveRefreshToken() {
        // given
        User success_testUserA = User.builder()
                .username("asd1234")
                .password("qwer1234!")
                .nickname("testUserA")
                .profileImgUrl("dd")
                .build();
        RefreshToken success_refreshToken = RefreshToken.builder()
                .token("testTokenA")
                .build();
        // when
        UserEntity result = repository.save(new UserEntity(success_testUserA, success_refreshToken));
        // then
        assertThat(result.getUsername()).isEqualTo(success_testUserA.getUsername());
        assertThat(result.getRefreshTokenEntity().getToken()).isEqualTo(success_refreshToken.getToken());
    }
}
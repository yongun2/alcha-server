package pcrc.alcha.application.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcrc.alcha.application.domain.auth.RefreshToken;
import pcrc.alcha.application.domain.auth.User;
import pcrc.alcha.application.domain.img.ImageType;
import pcrc.alcha.application.domain.jwt.Token;
import pcrc.alcha.application.service.image.ImageService;
import pcrc.alcha.application.utils.JWTTokenUtils;
import pcrc.alcha.exception.AlchaException;
import pcrc.alcha.exception.MessageType;
import pcrc.alcha.infrastructure.persistance.entity.RefreshTokenEntity;
import pcrc.alcha.infrastructure.persistance.entity.UserEntity;
import pcrc.alcha.infrastructure.persistance.repository.auth.UserRepository;

import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserOperationUseCase, UserReadUseCase {

    private final ImageService imageService;
    private final UserRepository repository;

    private final PasswordEncoder passwordEncoder;
    private final JWTTokenUtils jwtTokenUtils;


    private final String VALID_USERNAME_PATTERN = "^[a-z0-9_-]{2,16}$";
    private final String VALID_PASSWORD_PATTERN = "^(?=.*[a-zA-Z])" +
            "(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?])" +
            "[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?]{8,18}$";

    @Override
    @Transactional
    public FindUserResult register(UserCreateCommand command) {

        validateCommand(command);

        String imageSaveUrl = imageService.save(command.profileImgBase64(), ImageType.PROFILE);

        User user = User.builder()
                .username(command.username())
                .password(passwordEncoder.encode(command.password()))
                .nickname(command.nickname())
                .profileImgUrl(imageSaveUrl)
                .build();

        return FindUserResult.findByUser(
                repository.save(new UserEntity(user)).toUser()
        );
    }

    @Override
    @Transactional
    public FindLoginResult login(UserLoginCommand command) {

        UserEntity userEntity = repository.findUserEntityByUsername(command.username())
                .orElseThrow(() -> new AlchaException(MessageType.USER_NOT_FOUND));

        if (!passwordEncoder.matches(command.password(), userEntity.getPassword())) {
            throw new AlchaException(MessageType.USER_NOT_FOUND);
        }

        Token token = jwtTokenUtils.generateToken(userEntity.getNickname());

        log.info(userEntity.toUser().toString());
        UserEntity result = repository.save(new UserEntity(
                userEntity.toUser(),
                new RefreshTokenEntity(
                        RefreshToken.builder()
                                .token(token.refreshToken())
                                .build()
                )
        ));

        return FindLoginResult.findByLoginResult(
                token.accessToken(),
                result.getRefreshTokenEntity().getId()
        );
    }

    @Override
    @Transactional
    public void logout(UserFindQuery query) {
        repository.save(new UserEntity(
                repository.findUserEntityByNickname(query.nickname())
                        .orElseThrow(() -> new AlchaException(MessageType.USER_NOT_FOUND))
                        .toUser(),
                null
        ));
    }

    @Override
    public FindUserResult getUserByUsername(UserFindQuery query) {
        return FindUserResult.findByUser(
                repository.findUserEntityByUsername(query.username())
                        .orElseThrow(() -> new AlchaException(MessageType.USER_NOT_FOUND))
                        .toUser()
        );
    }

    @Override
    public FindUserResult getUserByNickname(UserFindQuery query) {
        return FindUserResult.findByUser(
                repository.findUserEntityByNickname(query.nickname())
                        .orElseThrow(() -> new AlchaException(MessageType.USER_NOT_FOUND))
                        .toUser()
        );
    }

    @Override
    public FindUserResult checkDuplicate(UserFindQuery query) {
        if (query.username() != null) {
            repository.findUserEntityByUsername(query.username())
                    .ifPresent((userEntity) -> {
                        throw new AlchaException(MessageType.DUPLICATED_USERNAME);
                    });
            return FindUserResult.findByUsername(query.username());
        }

        if (query.nickname() != null) {
            repository.findUserEntityByNickname(query.nickname())
                    .ifPresent((userEntity) -> {
                        throw new AlchaException(MessageType.DUPLICATED_USERNAME);
                    });
            return FindUserResult.findByNickname(query.nickname());
        }

        throw new AlchaException(MessageType.INTERNAL_SERVER_ERROR);
    }

    private void validateCommand(UserCreateCommand command) {
        if (!isValidUsername(command.username())) {
            throw new AlchaException(MessageType.BAD_USERNAME_PATTERN);
        }
        if (!isValidPassword(command.password())) {
            throw new AlchaException(MessageType.BAD_PASSWORD_PATTERN);
        }
        if (!isValidNickname(command.nickname())) {
            throw new AlchaException(MessageType.BAD_NICKNAME_PATTERN);
        }
    }

    private boolean isValidUsername(String username) {
        return Pattern.compile(VALID_USERNAME_PATTERN)
                .matcher(username)
                .matches();
    }

    private boolean isValidPassword(String password) {
        return Pattern.compile(VALID_PASSWORD_PATTERN)
                .matcher(password)
                .matches();
    }

    private boolean isValidNickname(String nickname) {
        return !nickname.isBlank() && nickname.length() >= 2 && nickname.length() <= 12;
    }


}

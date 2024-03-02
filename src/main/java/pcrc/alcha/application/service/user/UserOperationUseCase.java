package pcrc.alcha.application.service.user;

import lombok.Builder;
import pcrc.alcha.application.service.user.UserReadUseCase.FindUserResult;

import static pcrc.alcha.application.service.user.UserReadUseCase.*;

public interface UserOperationUseCase {

    FindUserResult register(UserCreateCommand command);
    FindLoginResult login(UserLoginCommand command);
    void logout(UserFindQuery query);

    @Builder
    record UserCreateCommand(
            String username,
            String password,
            String nickname,
            String profileImgBase64
    ) {

    }

    @Builder
    record UserLoginCommand(
            String username,
            String password
    ) {
    }
}

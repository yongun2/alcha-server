package pcrc.alcha.application.service.user;

import lombok.Builder;

public interface UserOperationUseCase {

    UserReadUseCase.FindUserResult register(UserCreateCommand command);

    @Builder
    record UserCreateCommand(
            String username,
            String password,
            String nickname,
            String profileImgBase64
    ) {

    }
}

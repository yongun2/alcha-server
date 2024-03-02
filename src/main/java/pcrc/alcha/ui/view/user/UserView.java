package pcrc.alcha.ui.view.user;

import static pcrc.alcha.application.service.user.UserReadUseCase.FindUserResult;

public record UserView(long id, String username, String nickname, String profileImgUrl) {
    public UserView(FindUserResult result) {
        this(result.userId(), result.username(), result.nickname(), result.profileImgUrl());
    }
}

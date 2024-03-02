package pcrc.alcha.ui.view.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import static pcrc.alcha.application.service.user.UserReadUseCase.FindUserResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserView(long id, String username, String nickname, String profileImgUrl) {
    public UserView(FindUserResult result) {
        this(result.userId(), result.username(), result.nickname(), result.profileImgUrl());
    }
}

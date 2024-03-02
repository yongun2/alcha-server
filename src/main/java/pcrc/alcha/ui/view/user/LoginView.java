package pcrc.alcha.ui.view.user;

import com.fasterxml.jackson.annotation.JsonInclude;

import static pcrc.alcha.application.service.user.UserReadUseCase.FindLoginResult;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginView(String accessToken, long refreshTokenId) {
    public LoginView(FindLoginResult result) {
        this(result.accessToken(), result.refreshTokenId());
    }
}

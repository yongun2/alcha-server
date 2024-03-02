package pcrc.alcha.ui.requestBody;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(@NotBlank String username, @NotBlank String password) {
}

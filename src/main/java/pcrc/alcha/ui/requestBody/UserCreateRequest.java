package pcrc.alcha.ui.requestBody;

import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest (@NotBlank String username, @NotBlank String password, @NotBlank String nickname, String profileImageBase64){}

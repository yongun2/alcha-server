package pcrc.alcha.application.domain.jwt;

import lombok.Builder;

@Builder
public record Token(String accessToken, String refreshToken) {
}

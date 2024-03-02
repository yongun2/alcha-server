package pcrc.alcha.application.domain.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
public class RefreshToken {
    private final long id;
    private final String token;
}

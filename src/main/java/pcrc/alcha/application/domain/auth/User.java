package pcrc.alcha.application.domain.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class User {
    private final long id;
    private final String username;
    private String password;
    private final String nickname;
    private final String profileImgUrl;
}

package pcrc.alcha.application.filter;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTAuthenticationToken extends AbstractAuthenticationToken {
    private String nickname;

    public JWTAuthenticationToken(String nickname, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.nickname = nickname;
        this.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.nickname;
    }
}

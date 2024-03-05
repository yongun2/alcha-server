package pcrc.alcha.utils;


import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import pcrc.alcha.application.domain.auth.User;
import pcrc.alcha.application.filter.JWTAuthenticationToken;

public class WithJWTMockUserSecurityContextFactory implements WithSecurityContextFactory<WithCustomMockUser> {
    @Override
    public SecurityContext createSecurityContext(WithCustomMockUser mockUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User details = User.builder()
                .id(mockUser.id())
                .username(mockUser.username())
                .nickname(mockUser.nickname())
                .build();
        JWTAuthenticationToken authenticationToken = new JWTAuthenticationToken(mockUser.nickname(), null);
        authenticationToken.setDetails(details);

        context.setAuthentication(authenticationToken);

        return context;
    }
}

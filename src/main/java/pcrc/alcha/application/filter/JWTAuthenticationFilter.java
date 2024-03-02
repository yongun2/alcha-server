package pcrc.alcha.application.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pcrc.alcha.application.utils.JWTTokenUtils;
import pcrc.alcha.exception.AlchaException;
import pcrc.alcha.exception.MessageType;
import pcrc.alcha.infrastructure.persistance.entity.UserEntity;
import pcrc.alcha.infrastructure.persistance.repository.auth.UserRepository;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JWTTokenUtils jwtTokenUtils;

    private final String JWT_CLAIM_NAME = "nickname";
    public static final String JWT_HEADER = "Authorization";

    private final String[] shouldNotFilterList = {
            "/api/v1/users/register",
            "/api/v1/users/login",
            "/api/v1/users/check-duplicate"
    };


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader(JWT_HEADER);
        log.info("JWTAuthenticationFilter = {}", request.getPathInfo());
        try {
            if (accessToken == null) {
                throw new AuthenticationServiceException("AccessToken required: " + request.getMethod());
            } else {

                UserEntity userDetails = userRepository.findUserEntityByNickname(
                                (String) jwtTokenUtils.parseClaims(accessToken)
                                        .get(JWT_CLAIM_NAME)
                        )
                        .orElseThrow(() -> new AlchaException(MessageType.USER_NOT_FOUND));

                JWTAuthenticationToken result = new JWTAuthenticationToken(userDetails.getNickname(), null);
                result.setDetails(userDetails.toUser());
                result.setAuthenticated(true);
                SecurityContextHolder.getContext().setAuthentication(result);
            }
        } catch (AuthenticationServiceException | AlchaException err) {
            log.info(err.getMessage());
        }

        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        for (String path : shouldNotFilterList) {
            if (request.getPathInfo().equals(path))
                return true;
        }
        return false;
    }

}

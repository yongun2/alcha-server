package pcrc.alcha.application.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pcrc.alcha.application.domain.jwt.Token;
import pcrc.alcha.exception.AlchaException;
import pcrc.alcha.exception.MessageType;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JWTTokenUtils {
    private final SecretKey key;

    @Value("${jwt.access-token-expiration}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token-expiration}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    public JWTTokenUtils(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Token generateToken(String nickname) {
        Date now = new Date();

        String accessToken = Jwts.builder()
                .subject("Alcha")
                .claim("nickname", nickname)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .subject("Alcha")
                .claim("nickname", nickname)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(key)
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new AlchaException(MessageType.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new AlchaException(MessageType.INVALID_TOKEN);
        }
    }
}

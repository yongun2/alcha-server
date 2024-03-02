package pcrc.alcha.infrastructure.persistance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.alcha.application.domain.auth.RefreshToken;

@Getter
@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor
public class RefreshTokenEntity {
    @Id
    @Column(name = "refresh_token_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String token;

    public RefreshTokenEntity(RefreshToken refreshToken) {
        this.token = refreshToken.getToken();
    }
}

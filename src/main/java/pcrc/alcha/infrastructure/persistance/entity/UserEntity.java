package pcrc.alcha.infrastructure.persistance.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.alcha.application.domain.auth.RefreshToken;
import pcrc.alcha.application.domain.auth.User;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class UserEntity {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    private String password;

    private String nickname;

    private String profileImgUrl;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "refresh_token_id")
    private RefreshTokenEntity refreshTokenEntity;

    public UserEntity(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.profileImgUrl = user.getProfileImgUrl();
    }

    public UserEntity(User user, RefreshTokenEntity refreshTokenEntity) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.nickname = user.getNickname();
        this.profileImgUrl = user.getProfileImgUrl();
        this.refreshTokenEntity = refreshTokenEntity;
    }

    public User toUser() {
        return User.builder()
                .id(this.id)
                .username(this.username)
                .password(this.password)
                .nickname(this.nickname)
                .profileImgUrl(this.profileImgUrl)
                .build();
    }

    public RefreshToken toRefreshToken() {
        return RefreshToken.builder()
                .id(refreshTokenEntity.getId())
                .token(refreshTokenEntity.getToken())
                .build();
    }
}

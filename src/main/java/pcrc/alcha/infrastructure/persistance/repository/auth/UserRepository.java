package pcrc.alcha.infrastructure.persistance.repository.auth;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import pcrc.alcha.infrastructure.persistance.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
    @Query("select u from UserEntity u left join fetch u.refreshTokenEntity where u.username = :username")
    Optional<UserEntity> findUserEntityByUsername(String username);
    @Query("select u from UserEntity u left join fetch u.refreshTokenEntity where u.nickname = :nickname")
    Optional<UserEntity> findUserEntityByNickname(String nickname);
}

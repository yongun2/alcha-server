package pcrc.alcha.infrastructure.persistance.repository.nest;

import org.springframework.data.jpa.repository.JpaRepository;
import pcrc.alcha.infrastructure.persistance.entity.nest.NestParticipantEntity;

import java.util.Optional;

public interface NestParticipantRepository extends JpaRepository<NestParticipantEntity, Long> {

    Optional<NestParticipantEntity> findNestParticipantEntityByUserEntity_Nickname(String nickname);
}

package pcrc.alcha.infrastructure.persistance.repository.nest;

import org.springframework.data.jpa.repository.JpaRepository;
import pcrc.alcha.infrastructure.persistance.entity.nest.NestEntity;

public interface NestRepository extends JpaRepository<NestEntity, Long> {
}

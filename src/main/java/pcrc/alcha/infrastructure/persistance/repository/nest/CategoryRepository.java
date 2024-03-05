package pcrc.alcha.infrastructure.persistance.repository.nest;

import org.springframework.data.repository.CrudRepository;
import pcrc.alcha.infrastructure.persistance.entity.CategoryEntity;

public interface CategoryRepository extends CrudRepository<CategoryEntity, Long> {
}

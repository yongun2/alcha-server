package pcrc.alcha.application.service.nest;

import lombok.Builder;
import pcrc.alcha.application.domain.nest.Category;
import pcrc.alcha.application.domain.nest.Nest;

import java.time.LocalDateTime;
import java.util.List;

public interface NestReadUseCase {

    List<FindNestResult> getAllNests();

    @Builder
    record NestFindQuery(int id) {
    }

    @Builder
    record FindNestResult(
            long id,
            String name,
            String description,
            String thumbnailUrl,
            int maxUserNum,
            int fines,
            String managerAccount,
            LocalDateTime createdDateTime,
            Category category
    ) {
        public static FindNestResult findByNest(Nest nest) {
            return FindNestResult.builder()
                    .id(nest.getId())
                    .name(nest.getName())
                    .thumbnailUrl(nest.getThumbnailUrl())
                    .description(nest.getDescription())
                    .maxUserNum(nest.getMaxUserNum())
                    .fines(nest.getFines())
                    .managerAccount(nest.getManagerAccount())
                    .createdDateTime(nest.getCreatedDateTime())
                    .build();
        }
        public static FindNestResult findByNestWithCategory(Nest nest, Category category) {
            return FindNestResult.builder()
                    .id(nest.getId())
                    .name(nest.getName())
                    .thumbnailUrl(nest.getThumbnailUrl())
                    .description(nest.getDescription())
                    .maxUserNum(nest.getMaxUserNum())
                    .fines(nest.getFines())
                    .managerAccount(nest.getManagerAccount())
                    .createdDateTime(nest.getCreatedDateTime())
                    .category(category)
                    .build();
        }
    }

}

package pcrc.alcha.application.service.nest;

import lombok.Builder;
import pcrc.alcha.application.service.nest.NestReadUseCase.FindNestResult;

public interface NestOperationCase {

    FindNestResult create(NestCreateCommand command);

    @Builder
    record NestCreateCommand(
            String name,
            String description,
            String thumbnailBase64,
            long categoryId,
            int maxUserNum,
            int fines,
            String managerAccount
    ) {

    }
}

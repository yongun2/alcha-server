package pcrc.alcha.application.service.nest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pcrc.alcha.application.domain.auth.User;
import pcrc.alcha.application.domain.img.ImageType;
import pcrc.alcha.application.domain.nest.Nest;
import pcrc.alcha.application.domain.nest.NestParticipant;
import pcrc.alcha.application.domain.nest.Role;
import pcrc.alcha.application.service.image.ImageService;
import pcrc.alcha.exception.AlchaException;
import pcrc.alcha.exception.MessageType;
import pcrc.alcha.infrastructure.persistance.entity.CategoryEntity;
import pcrc.alcha.infrastructure.persistance.entity.UserEntity;
import pcrc.alcha.infrastructure.persistance.entity.nest.NestEntity;
import pcrc.alcha.infrastructure.persistance.entity.nest.NestParticipantEntity;
import pcrc.alcha.infrastructure.persistance.repository.nest.CategoryRepository;
import pcrc.alcha.infrastructure.persistance.repository.nest.NestParticipantRepository;
import pcrc.alcha.infrastructure.persistance.repository.nest.NestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NestService implements NestOperationCase, NestReadUseCase {

    private final NestParticipantRepository nestParticipantRepository;
    private final NestRepository nestRepository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    @Override
    @Transactional
    public FindNestResult create(NestCreateCommand command) {

        Nest.NestBuilder nestBuilder = Nest.builder()
                .name(command.name())
                .description(command.description())
                .maxUserNum(command.maxUserNum())
                .fines(command.fines())
                .managerAccount(command.managerAccount())
                .createdDateTime(LocalDateTime.now());

        CategoryEntity categoryEntity = categoryRepository.findById(command.categoryId())
                .orElseThrow(() -> new AlchaException(MessageType.CATEGORY_NOT_FOUNT));

        if (command.thumbnailBase64() != null) {
            String thumbnailUrl = imageService.save(command.thumbnailBase64(), ImageType.THUMBNAIL);
            nestBuilder.thumbnailUrl(thumbnailUrl);
        } else {
            nestBuilder.thumbnailUrl(categoryEntity.getImgUrl());
        }
        Nest nest = nestBuilder.build();

        NestParticipant participantInfo = NestParticipant.builder()
                .accepted(true)
                .role(Role.MANAGER)
                .build();

        User details = getAuthenticatedDetails();

        NestParticipantEntity result = nestParticipantRepository.save(
                new NestParticipantEntity(
                        new UserEntity(details),
                        participantInfo,
                        new NestEntity(nest, categoryEntity)
                )
        );

        return FindNestResult.findByNestWithCategory(
                result.getNestEntity().toNest(),
                categoryEntity.toCategory()
        );
    }
    @Override
    public List<FindNestResult> getAllNests() {
        return nestRepository.findAll()
                .stream()
                .map((nestEntity -> FindNestResult.findByNestWithCategory(
                        nestEntity.toNest(), nestEntity.getCategoryEntity().toCategory()
                )))
                .collect(Collectors.toList());
    }

    private User getAuthenticatedDetails() {
        try {
            return (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        } catch (Exception err) {
            throw new AlchaException(MessageType.INTERNAL_SERVER_ERROR);
        }
    }
}

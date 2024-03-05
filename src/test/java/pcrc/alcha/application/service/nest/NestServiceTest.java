package pcrc.alcha.application.service.nest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import pcrc.alcha.application.domain.nest.Nest;
import pcrc.alcha.infrastructure.persistance.entity.CategoryEntity;
import pcrc.alcha.infrastructure.persistance.entity.nest.NestEntity;
import pcrc.alcha.infrastructure.persistance.entity.nest.NestParticipantEntity;
import pcrc.alcha.infrastructure.persistance.repository.nest.CategoryRepository;
import pcrc.alcha.infrastructure.persistance.repository.nest.NestParticipantRepository;
import pcrc.alcha.infrastructure.persistance.repository.nest.NestRepository;
import pcrc.alcha.utils.WithCustomMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static pcrc.alcha.application.service.nest.NestOperationCase.NestCreateCommand;

@Slf4j
@SpringBootTest
class NestServiceTest {

    @Autowired
    NestOperationCase operationCase;

    @Autowired
    NestReadUseCase readUseCase;

    @Autowired
    NestParticipantRepository nestParticipantRepository;

    @Autowired
    NestRepository nestRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Value("${local.img.nest.category.study}")
    private String DEFAULT_CATEGORY_STUDY_IMAGE_URL;

    private final long EXERCISE_CATEGORY_ID = 1;
    private final long STUDY_CATEGORY_ID = 2;

    @Test
    @Transactional
    @DisplayName("둥지 생성 테스트")
    @WithCustomMockUser
    void create() {
        // given
        NestCreateCommand command = NestCreateCommand.builder()
                .name("😎알고리즘 스터디룸")
                .description("JAVA 알고리즘 스터디룸 입니다.")
                .maxUserNum(4)
                .managerAccount("국민 112-323-123392")
                .categoryId(STUDY_CATEGORY_ID)
                .thumbnailBase64(null)
                .fines(30000)
                .build();
        // when
        NestReadUseCase.FindNestResult result = operationCase.create(command);
        Optional<NestParticipantEntity> participantEntityOptional = nestParticipantRepository.findNestParticipantEntityByUserEntity_Nickname("testUserA");
        // then
        assertThat(result.name()).isEqualTo(command.name());
        assertThat(result.thumbnailUrl()).isEqualTo(DEFAULT_CATEGORY_STUDY_IMAGE_URL);
        assertThat(participantEntityOptional.isPresent()).isTrue();
        assertThat(participantEntityOptional.get().getUserEntity().getNickname()).isEqualTo("testUserA");
    }

    @Test
    @Transactional
    @DisplayName("둥지 생성 테스트")
    void getAllNests() {
        // given
        Nest nestA = Nest.builder()
                .name("😎알고리즘 스터디룸")
                .description("JAVA 알고리즘 스터디룸 입니다.")
                .maxUserNum(4)
                .managerAccount("국민 112-323-123392")
                .thumbnailUrl("helloA")
                .createdDateTime(LocalDateTime.now())
                .fines(30000)
                .build();
        Nest nestB = Nest.builder()
                .name("😎Spring Boot 스터디룸")
                .description("Spring Boot 스터디룸 입니다.")
                .maxUserNum(2)
                .managerAccount("신한 112-323-123392")
                .thumbnailUrl("helloB")
                .createdDateTime(LocalDateTime.now())
                .fines(20000)
                .build();
        Nest nestC = Nest.builder()
                .name("😎Python 스터디룸")
                .description("Python 알고리즘 스터디룸 입니다.")
                .maxUserNum(4)
                .managerAccount("국민 112-323-123392")
                .createdDateTime(LocalDateTime.now())
                .thumbnailUrl("helloA")
                .fines(30000)
                .build();

        Optional<CategoryEntity> studyCategory = categoryRepository.findById(STUDY_CATEGORY_ID);
        Optional<CategoryEntity> exerciseCategory = categoryRepository.findById(EXERCISE_CATEGORY_ID);
        assertThat(studyCategory.isPresent()).isTrue();
        assertThat(exerciseCategory.isPresent()).isTrue();

        // when
        nestRepository.save(new NestEntity(nestA, studyCategory.get()));
        nestRepository.save(new NestEntity(nestB, exerciseCategory.get()));
        nestRepository.save(new NestEntity(nestC, studyCategory.get()));
        // then
        List<NestReadUseCase.FindNestResult> results = readUseCase.getAllNests();

        assertThat(results.size()).isEqualTo(3);
        for (NestReadUseCase.FindNestResult result : results) {
            log.info(result.toString());
        }


    }
}
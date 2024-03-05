package pcrc.alcha.infrastructure.persistance.entity.nest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.alcha.application.domain.nest.Nest;
import pcrc.alcha.infrastructure.persistance.entity.CategoryEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "nests")
@NoArgsConstructor
public class NestEntity {
    @Id
    @Column(name = "nest_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    private String thumbnailUrl;
    private String description;
    private int maxUserNum;
    private int fines;
    private String managerAccount;
    private LocalDateTime createdDateTime;

    @ManyToOne
    @JoinColumn(name = "nest_category_id")
    private CategoryEntity categoryEntity;

    @OneToMany(mappedBy = "nestEntity")
    private final List<NestParticipantEntity> nestParticipantEntityList = new ArrayList<>();


    public NestEntity(Nest nest, CategoryEntity categoryEntity) {
        this.id = nest.getId();
        this.name = nest.getName();
        this.thumbnailUrl = nest.getThumbnailUrl();
        this.description = nest.getDescription();
        this.maxUserNum = nest.getMaxUserNum();
        this.fines = nest.getFines();
        this.managerAccount = nest.getManagerAccount();
        this.createdDateTime = nest.getCreatedDateTime();
        this.categoryEntity = categoryEntity;
    }

    public Nest toNest() {
        return Nest.builder()
                .id(this.id)
                .name(this.name)
                .thumbnailUrl(this.thumbnailUrl)
                .description(this.description)
                .maxUserNum(this.maxUserNum)
                .fines(this.fines)
                .managerAccount(this.managerAccount)
                .createdDateTime(this.createdDateTime)
                .build();
    }


}

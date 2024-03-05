package pcrc.alcha.infrastructure.persistance.entity.nest;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.alcha.application.domain.auth.User;
import pcrc.alcha.application.domain.nest.NestParticipant;
import pcrc.alcha.application.domain.nest.Role;
import pcrc.alcha.infrastructure.persistance.entity.UserEntity;

@Getter
@Entity
@Table(name = "nest_participants")
@NoArgsConstructor
public class NestParticipantEntity {
    @Id
    @Column(name = "nest_participant_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean accepted;
    @Enumerated(value = EnumType.STRING)
    private Role role;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nest_id")
    NestEntity nestEntity;

    @ManyToOne
    @JoinColumn(name = "user_id")
    UserEntity userEntity;

    public NestParticipantEntity(UserEntity userEntity, NestParticipant nestParticipant, NestEntity nestEntity) {
        this.id = nestParticipant.getId();
        this.role = nestParticipant.getRole();
        this.accepted = nestParticipant.isAccepted();
        this.userEntity = userEntity;
        this.nestEntity = nestEntity;
    }
}

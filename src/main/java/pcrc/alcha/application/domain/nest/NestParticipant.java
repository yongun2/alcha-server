package pcrc.alcha.application.domain.nest;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class NestParticipant {
    private final long id;
    private final boolean accepted;
    private final Role role;
}

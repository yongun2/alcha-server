package pcrc.alcha.application.domain.plan;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class WeeklyFine {
    private final long id;
    private final String participantNickname;
    private final int amount;
    private final boolean isPaid;
}

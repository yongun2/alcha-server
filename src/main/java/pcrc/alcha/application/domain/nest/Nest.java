package pcrc.alcha.application.domain.nest;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class Nest {
    private final long id;
    private final String name;
    private final String description;
    private final int maxUserNum;
    private final int fines;
    private final String managerAccount;
    private final LocalDateTime createdDateTime;
    private final String thumbnailUrl;
}

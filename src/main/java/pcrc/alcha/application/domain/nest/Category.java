package pcrc.alcha.application.domain.nest;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class Category {
    private final long id;
    private final String title;
    private final String imgUrl;
}

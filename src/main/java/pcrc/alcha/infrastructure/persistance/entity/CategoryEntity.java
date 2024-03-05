package pcrc.alcha.infrastructure.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pcrc.alcha.application.domain.nest.Category;

@Getter
@Entity
@Table(name = "nest_categories")
@NoArgsConstructor
public class CategoryEntity {
    @Id
    @Column(name = "nest_category_id")
    private long id;

    private String title;
    private String imgUrl;

    public CategoryEntity(Category category) {
        this.id = category.getId();
        this.title = category.getTitle();
        this.imgUrl = category.getImgUrl();
    }

    public Category toCategory() {
        return Category.builder()
                .id(this.id)
                .title(this.title)
                .imgUrl(this.imgUrl)
                .build();
    }
}

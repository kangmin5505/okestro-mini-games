package okestro.internproject.domain.game.entity.db;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.utils.GameTitleConverter;
import okestro.internproject.global.entity.BaseDateTimeEntity;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Game extends BaseDateTimeEntity {

    @Id
    @Column(nullable = false, unique = true)
    @Enumerated(EnumType.STRING)
    @Convert(converter = GameTitleConverter.class)
    private GameTitle title;

    public String getTitle() {
        return title.getTitle();
    }
}

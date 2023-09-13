package okestro.internproject.domain.game.entity.memory;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class CardItem {
    private String value;
    private boolean visible;
    private Integer position;
    private boolean matched;
    private UUID matchedBy = null;

    @Builder
    public CardItem(String value, boolean visible, Integer position, boolean matched) {
        this.value = value;
        this.visible = visible;
        this.position = position;
        this.matched = matched;
    }
}

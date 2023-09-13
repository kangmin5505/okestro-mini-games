package okestro.internproject.domain.game.dto.cardMatching;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class CardDto {

    private String value;
    private boolean visible;
    private Integer position;
    private boolean matched;
    private UUID matchedBy = null;

    @Builder
    public CardDto(String value, boolean visible, Integer position, boolean matched) {
        this.value = value;
        this.visible = visible;
        this.position = position;
        this.matched = matched;
    }
}

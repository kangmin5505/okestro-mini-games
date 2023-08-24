package okestro.internproject.domain.game.dto.cardMatching;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardMatchDto {
    private int position;
    private String faceValue;

}

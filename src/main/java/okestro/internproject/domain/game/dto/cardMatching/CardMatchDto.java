package okestro.internproject.domain.game.dto.cardMatching;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardMatchDto {
    private int position;
    private String faceValue;
}

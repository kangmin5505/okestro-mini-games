package okestro.internproject.domain.game.dto.gomoku;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GomokuStoneDto {
    private int row;
    private int col;
}

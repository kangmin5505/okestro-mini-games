package okestro.internproject.domain.game.dto.gomoku;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import okestro.internproject.domain.game.entity.memory.GomokuInstance;

import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class BoardDto {
    private final UUID blackUserId;
    private final UUID whiteUserId;

    public static BoardDto createBoardDto(GomokuInstance gameInstance) {
        return BoardDto.builder()
                .blackUserId(gameInstance.getBlackUserId())
                .whiteUserId(gameInstance.getWhiteUserId())
                .build();
    }
}

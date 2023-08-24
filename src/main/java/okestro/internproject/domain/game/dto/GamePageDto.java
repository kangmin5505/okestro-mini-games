package okestro.internproject.domain.game.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GamePageDto {
    private List<GameDto> games;
    private long totalElements;
}

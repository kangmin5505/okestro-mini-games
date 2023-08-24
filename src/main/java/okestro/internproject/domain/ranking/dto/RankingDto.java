package okestro.internproject.domain.ranking.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RankingDto {
    private final UUID userId;
    private final String nickname;
    private final Integer rank;
    private final Integer wins;
    private final Integer loses;
    private final Integer winPercentage;
    private final Integer totalGames;
}

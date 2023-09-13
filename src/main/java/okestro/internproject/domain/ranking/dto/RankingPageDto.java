package okestro.internproject.domain.ranking.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RankingPageDto {
    private List<RankingDto> rankings;
    private long totalElements;
}

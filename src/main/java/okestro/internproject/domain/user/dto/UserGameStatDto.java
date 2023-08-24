package okestro.internproject.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserGameStatDto {
    private String gameTitle;
    private Integer wins;
    private Integer loses;
}

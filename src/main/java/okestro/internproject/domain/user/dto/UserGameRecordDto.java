package okestro.internproject.domain.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserGameRecordDto {
    private String gameTitle;
    private String winUserNickname;
    private String loseUserNickname;
    private Integer winUserScore;
    private Integer loseUserScore;
    private LocalDateTime startTime;
    private LocalDateTime finishTime;
}

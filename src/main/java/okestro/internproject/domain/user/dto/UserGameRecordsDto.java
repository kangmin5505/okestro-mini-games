package okestro.internproject.domain.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.user.entity.db.UserGameRecord;

import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class UserGameRecordsDto {
    private final List<UserGameRecordDto> userGameRecords;

    public static UserGameRecordsDto createUserGameRecordsDto(List<UserGameRecord> UserGameRecords) {
        List<UserGameRecordDto> collect = UserGameRecords.stream()
                .map((gameRecord) ->
                        UserGameRecordDto.builder()
                                .gameTitle(gameRecord.getGame().getTitle())
                                .winUserNickname(gameRecord.getWinUser().getNickname())
                                .loseUserNickname(gameRecord.getLoseUser().getNickname())
                                .winUserScore(gameRecord.getWinUserScore())
                                .loseUserScore(gameRecord.getLoseUserScore())
                                .startTime(gameRecord.getStartTime())
                                .finishTime(gameRecord.getFinishTime())
                                .build()
                )
                .collect(Collectors.toList());
        return new UserGameRecordsDto(collect);
    }
}

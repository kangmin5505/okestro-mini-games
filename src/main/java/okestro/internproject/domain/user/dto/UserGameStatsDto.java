package okestro.internproject.domain.user.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.game.entity.db.Game;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.user.entity.db.UserGameRecord;
import okestro.internproject.domain.user.entity.db.UserGameStat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
public class UserGameStatsDto {
    private final List<UserGameStatDto> userGameStats;

    public static UserGameStatsDto createEmptyUserGameStatsDto() {
        List<UserGameStatDto> userGameStatDtos = new ArrayList<>();
        for (GameTitle gameTitle : GameTitle.values()) {
            userGameStatDtos.add(UserGameStatDto.builder()
                    .gameTitle(gameTitle.getTitle())
                    .wins(0)
                    .loses(0)
                    .build());
        }
        return new UserGameStatsDto(userGameStatDtos);
    }

    public static UserGameStatsDto createUserGameStatsDto(List<UserGameStat> userGameStats) {
        List<UserGameStatDto> userGameStatDtos = userGameStats.stream()
                .map((userGameStat) ->
                        UserGameStatDto.builder()
                                .gameTitle(userGameStat.getGame().getTitle())
                                .wins(userGameStat.getWins())
                                .loses(userGameStat.getLoses())
                                .build()
                )
                .collect(Collectors.toList());
        return new UserGameStatsDto(userGameStatDtos);
    }

    public static UserGameStatsDto createUserGameStatsDto(List<UserGameRecord> userWinRecords,
                                                          List<UserGameRecord> userLoseRecords) {
        //TODO: literal 에서 수정하기
        Map<String, UserGameStatDto> gameStats = new HashMap<>();
        gameStats.put(GameTitle.CARD_MATCHING.getTitle(),
                new UserGameStatDto(GameTitle.CARD_MATCHING.getTitle(), 0, 0));
        userWinRecords.stream()
                .map(UserGameRecord::getGame)
                .map(Game::getTitle)
                .forEach(gameTitle -> {
                    UserGameStatDto userGameStatDto = gameStats.get(gameTitle);
                    userGameStatDto.setWins(userGameStatDto.getWins() + 1);
                });
        userLoseRecords.stream()
                .map(UserGameRecord::getGame)
                .map(Game::getTitle)
                .forEach(gameTitle -> {
                    UserGameStatDto userGameStatDto = gameStats.get(gameTitle);
                    userGameStatDto.setLoses(userGameStatDto.getLoses() + 1);
                });
        return new UserGameStatsDto(new ArrayList<>(gameStats.values()));
    }
}

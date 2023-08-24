package okestro.internproject.domain.game.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import okestro.internproject.domain.game.entity.db.Game;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class GamesDto {
    private List<GameDto> games;

    public static GamesDto createGamesDto(List<Game> games) {
        List<GameDto> gameDtos = games.stream()
                .map((game) -> new GameDto(game.getTitle()))
                .collect(Collectors.toList());
        return new GamesDto(gameDtos);
    }
}

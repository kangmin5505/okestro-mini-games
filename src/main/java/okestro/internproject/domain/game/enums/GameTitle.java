package okestro.internproject.domain.game.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Optional;

@Getter
@ToString
@RequiredArgsConstructor
public enum GameTitle {
    GOMOKU("gomoku"),
    CARD_MATCHING("card-matching");

    private final String title;

    public static Optional<GameTitle> findByTitle(String title) {
        for (GameTitle gameTitle : GameTitle.values()) {
            if (gameTitle.getTitle().equals(title)) {
                return Optional.of(gameTitle);
            }
        }
        return Optional.empty();
    }
}

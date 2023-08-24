package okestro.internproject.domain.game.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum GameTitle {
    GOMOKU("gomoku"),
    CARD_MATCHING("card-matching");

    private final String title;
}

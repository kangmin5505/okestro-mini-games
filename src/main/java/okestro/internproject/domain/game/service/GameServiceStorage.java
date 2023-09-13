package okestro.internproject.domain.game.service;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.game.enums.GameTitle;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceStorage {
    private final GomokuService gomokuService;
    private final CardMatchingService cardMatchingService;

    public GameStompService getGameService(GameTitle gameTitle) {
        switch (gameTitle) {
            case GOMOKU:
                return gomokuService;
            case CARD_MATCHING:
                return cardMatchingService;
            default:
                return null;
        }
    }
}

package okestro.internproject.domain.game.entity.memory;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import okestro.internproject.domain.game.enums.GameTitle;

import java.util.UUID;

@Getter
@Builder
@ToString
public class GameRoom {
    private final GameRoomInfo gameRoomInfo;
    private GameInstance gameInstance;

    public void startGame(GameTitle gameTitle) {
        gameRoomInfo.setOnGame();
        switch (gameTitle) {
            case CARD_MATCHING:
                gameInstance = new CardMatchingInstance(gameRoomInfo.getPlayer1(), gameRoomInfo.getPlayer2());
                break;
            case GOMOKU:
                gameInstance = new GomokuInstance(gameRoomInfo.getPlayer1(), gameRoomInfo.getPlayer2());
                break;
        }
    }

    public void finishGame() {
        gameInstance.finishGame();
    }

    public UUID getWinnerId() {
        return gameInstance.getWinnerId();
    }

    public void clearGame() {
        gameRoomInfo.setOffGame();
        gameInstance = null;
    }

}

package okestro.internproject.domain.game.entity.memory;

import java.util.UUID;

public interface GameInstance {
    void startGame();

    void finishGame();

    UUID changeTurnId();

    UUID getWinnerId();

    void leaveUserOnGame(UUID userId);
}

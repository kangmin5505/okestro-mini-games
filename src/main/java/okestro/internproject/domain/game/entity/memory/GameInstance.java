package okestro.internproject.domain.game.entity.memory;

import okestro.internproject.domain.user.entity.SimpleUser;

import java.time.LocalDateTime;
import java.util.UUID;

public interface GameInstance {
    void startGame();

    void finishGame();

    UUID changeTurnId();

    UUID getWinnerId();

    void leaveUserOnGame(UUID userId);

    SimpleUser getPlayer1();

    SimpleUser getPlayer2();

    int getPlayer1Score();

    int getPlayer2Score();

    LocalDateTime getStartTime();

    LocalDateTime getFinishTime();


}

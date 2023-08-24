package okestro.internproject.domain.game.entity.memory;

import lombok.Builder;
import lombok.Data;
import okestro.internproject.domain.game.enums.State;
import okestro.internproject.domain.user.entity.SimpleUser;

import java.util.UUID;

@Data
public class GameRoomInfo {
    private final UUID id;
    private final String title;
    private Long userNum;
    private Long limitUserNum;
    private State state;
    private final UUID hostId;
    private final SimpleUser player1;
    private SimpleUser player2;
    private int readyCount = 0;

    @Builder
    public GameRoomInfo(UUID id, String title, Long userNum, Long limitUserNum, State state, UUID hostId, SimpleUser player1, SimpleUser player2) {
        this.id = id;
        this.title = title;
        this.userNum = userNum;
        this.limitUserNum = limitUserNum;
        this.state = state;
        this.hostId = hostId;
        this.player1 = player1;
        this.player2 = player2;
    }

    public void joinUser(SimpleUser player2) {
        this.player2 = player2;
        this.userNum++;
    }

    public void exitUser() {
        this.player2 = null;
        this.userNum--;
        readyCount = 0;
    }

    public boolean isReadyAllPlayers() {
        return readyCount == 1;
    }

    public void readyToggle(UUID userId) {
        if (userId.equals(player2.getId())) {
            readyCount = isReadyAllPlayers() ? 0 : 1;
        }
    }

    public boolean isEnoughPlayers() {
        return userNum.equals(limitUserNum);
    }

    public boolean isHost(SimpleUser user) {
        return user.getId().equals(hostId);
    }

    public boolean isCanJoin() {
        return (state == State.WAITING) && (userNum < limitUserNum);
    }

    public boolean isOnGame() {
        return state == State.ONGAME;
    }

    public void setOnGame() {
        state = State.ONGAME;
    }

    public void setOffGame() {
        state = State.WAITING;
        readyCount = 0;
    }
}

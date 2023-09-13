package okestro.internproject.domain.game.dto;

import lombok.Builder;
import lombok.Data;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.entity.memory.GameRoomInfo;
import okestro.internproject.domain.game.enums.State;
import okestro.internproject.domain.user.entity.SimpleUser;

import java.util.UUID;

@Data
public class GameRoomInfoDto {
    private final UUID id;
    private final String title;
    private Long userNum;
    private Long limitUserNum;
    private State state;
    private final UUID hostId;
    private final SimpleUser player1;
    private SimpleUser player2;

    @Builder
    public GameRoomInfoDto(UUID id, String title, Long userNum, Long limitUserNum, State state, UUID hostId, SimpleUser player1, SimpleUser player2) {
        this.id = id;
        this.title = title;
        this.userNum = userNum;
        this.limitUserNum = limitUserNum;
        this.state = state;
        this.hostId = hostId;
        this.player1 = player1;
        this.player2 = player2;
    }

    public static GameRoomInfoDto createGameRoomInfoDto(GameRoom gameRoom) {
        GameRoomInfo gameRoomInfo = gameRoom.getGameRoomInfo();
        return GameRoomInfoDto.builder()
                .id(gameRoomInfo.getId())
                .title(gameRoomInfo.getTitle())
                .userNum(gameRoomInfo.getUserNum())
                .limitUserNum(gameRoomInfo.getLimitUserNum())
                .state(gameRoomInfo.getState())
                .hostId(gameRoomInfo.getHostId())
                .player1(gameRoomInfo.getPlayer1())
                .player2(gameRoomInfo.getPlayer2())
                .build();
    }
}
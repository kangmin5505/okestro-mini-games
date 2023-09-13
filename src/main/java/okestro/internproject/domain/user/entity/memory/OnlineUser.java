package okestro.internproject.domain.user.entity.memory;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import okestro.internproject.domain.user.entity.SimpleUser;

import java.util.UUID;

@Getter
@Builder
@ToString
public class OnlineUser {
    private final SimpleUser user;
    private String gameTitle;
    private UUID gameRoomId;

    public void updateGameRoom(String gameTitle, UUID gameRoomId) {
        this.gameTitle = gameTitle;
        this.gameRoomId = gameRoomId;
    }
}

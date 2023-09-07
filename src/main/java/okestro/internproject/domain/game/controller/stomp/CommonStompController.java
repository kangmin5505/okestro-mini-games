package okestro.internproject.domain.game.controller.stomp;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.auth.jwt.dto.JwtUserDto;
import okestro.internproject.domain.game.dto.common.ChatDto;
import okestro.internproject.domain.game.dto.common.ReadyToggleDto;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.exception.GameErrorCode;
import okestro.internproject.domain.game.exception.GameException;
import okestro.internproject.domain.game.service.GameServiceStorage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CommonStompController {

    private final GameServiceStorage gameServiceStorage;

    @MessageMapping("/chat/{gameTitle}/{gameRoomId}")
    public ChatDto chatGameRoom(ChatDto chatDto) {
        return chatDto;
    }

    @MessageMapping("/room-maintain/{gameTitle}/{gameRoomId}/ready-toggle")
    @SendTo("/topic/room-maintain/{gameTitle}/{gameRoomId}")
    public ReadyToggleDto readyToggle(@DestinationVariable String gameTitle,
                                      @DestinationVariable UUID gameRoomId,
                                      JwtUserDto jwtUserDto) {
        GameTitle title = GameTitle.findByTitle(gameTitle)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));

        GameRoom gameRoom = gameServiceStorage.getGameService(title).findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));
        gameRoom.getGameRoomInfo().readyToggle(jwtUserDto.getId());
        return new ReadyToggleDto();
    }

    @MessageMapping("/room-maintain/{gameTitle}/{gameRoomId}")
    public String roomMaintain(String message) {
        return message;
    }
}

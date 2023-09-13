package okestro.internproject.domain.game.controller.stomp;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.game.dto.gomoku.GomokuStoneDto;
import okestro.internproject.domain.game.service.GomokuService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class GomokuStompController {

    private final GomokuService gomokuService;

    @MessageMapping("/gomoku/{gameRoomId}/put-stone")
    public void putStone(@DestinationVariable UUID gameRoomId, GomokuStoneDto gomokuStoneDto) {
        gomokuService.putStone(gameRoomId, gomokuStoneDto.getRow(), gomokuStoneDto.getCol());
    }

    @MessageMapping("/gomoku/{gameRoomId}/timeout")
    public void timeout(@DestinationVariable UUID gameRoomId) {
        gomokuService.turnTimeout(gameRoomId);
    }

    @MessageMapping("/gomoku/{gameRoomId}")
    public String gomoku(String payload) {
        return payload;
    }
}

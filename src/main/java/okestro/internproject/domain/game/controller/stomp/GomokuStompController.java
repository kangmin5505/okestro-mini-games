package okestro.internproject.domain.game.controller.stomp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.service.GomokuService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Controller
@Slf4j
@RequiredArgsConstructor
public class GomokuStompController {

    private final GomokuService gomokuService;

    @MessageMapping("/gomoku/{gameRoomId}")
    public void aboutGame(@DestinationVariable UUID gameRoomId, @Payload Map<String, Object> payload) {
        switch (payload.get("type").toString()) {
            case "TIMEOUT":
                gomokuService.turnTimeout(gameRoomId);
                break;
            case "PUT_STONE":
                try {
                    RowCol rowCol = getRowCol(payload.get("content"));

                    gomokuService.putStone(gameRoomId, rowCol.row, rowCol.col);
                } catch (ClassCastException e) {
                    log.error("ClassCastException");
                }
                break;
        }
    }

    private RowCol getRowCol(Object contentObject) {
        Map<String, Object> content = (Map<String, Object>) contentObject;

        Integer row = (Integer) content.get("row");
        Integer col = (Integer) content.get("col");
        return new RowCol(row, col);
    }

    static class RowCol {
        private final int row;
        private final int col;

        RowCol(int row, int col) {
            this.row = row;
            this.col = col;
        }
    }
}

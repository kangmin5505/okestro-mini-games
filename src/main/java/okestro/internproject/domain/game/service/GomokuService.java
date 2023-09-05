package okestro.internproject.domain.game.service;

import io.micrometer.core.annotation.Counted;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.dto.common.MessageDto;
import okestro.internproject.domain.game.dto.gomoku.BoardDto;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.entity.memory.GomokuInstance;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.enums.GomokuMessageType;
import okestro.internproject.domain.game.enums.PutResult;
import okestro.internproject.domain.game.exception.GameErrorCode;
import okestro.internproject.domain.game.exception.GameException;
import okestro.internproject.domain.game.repository.stompRepository.GomokuRepository;
import okestro.internproject.domain.user.service.OnlineUserService;
import okestro.internproject.domain.user.service.UserGameRecordService;
import okestro.internproject.domain.user.service.UserGameStatService;
import okestro.internproject.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class GomokuService extends GameStompService {

    private final GomokuRepository gomokuRepository;


    @Autowired
    public GomokuService(GomokuRepository gomokuRepository,
                         OnlineUserService onlineUserService,
                         SimpMessagingTemplate simpMessagingTemplate,
                         GameService gameService,
                         UserService userService,
                         UserGameRecordService userGameRecordService,
                         UserGameStatService userGameStatService) {
        super(gomokuRepository, onlineUserService, simpMessagingTemplate,
                gameService, userService, userGameRecordService, userGameStatService);
        this.gomokuRepository = gomokuRepository;
    }

    @Override
    @Counted("my.game.gomoku.start")
    public void startGame(UUID gameRoomId) {
        GameRoom gameRoom = gomokuRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));
        GomokuInstance gameInstance = (GomokuInstance) (gameRoom.startGame(GameTitle.GOMOKU)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_INSTANCE)));

        MessageDto messageDto = MessageDto.builder()
                .type(GomokuMessageType.INIT_BOARD)
                .content(BoardDto.createBoardDto(gameInstance))
                .build();
        simpMessagingTemplate.convertAndSend(String.format("/topic/gomoku/%s", gameRoomId), messageDto);
        changeTurn(gameInstance, gameRoomId);
    }

    public void putStone(UUID gameRoomId, int row, int col) {
        GameRoom gameRoom = gomokuRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));
        GomokuInstance gameInstance = (GomokuInstance) (gameRoom.getGameInstance()
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_INSTANCE)));

        PutResult putResult = gameInstance.putStone(row, col);

        Map<String, Object> content = new HashMap<>();
        content.put("row", row);
        content.put("col", col);
        content.put("stoneColor", gameInstance.getCurrentTurnStone().toString());
        content.put("putResult", putResult);

        MessageDto messageDto = MessageDto.builder()
                .type(GomokuMessageType.PUT_STONE)
                .content(content)
                .build();
        simpMessagingTemplate.convertAndSend(String.format("/topic/gomoku/%s", gameRoomId), messageDto);

        if (putResult == PutResult.FINISH) {
            finishGame(gameRoom, gameRoomId);
        } else if (putResult == PutResult.SUCCESS) {
            changeTurn(gameInstance, gameRoomId);
        }
    }

    @Override
    public void finishGame(GameRoom gameRoom, UUID gameRoomId) {
        gameRoom.finishGame();
        GomokuInstance gameInstance = (GomokuInstance) (gameRoom.getGameInstance()
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_INSTANCE)));
        saveResult(gameInstance, GameTitle.GOMOKU);

        Map<String, Object> content = new HashMap<>();
        content.put("winnerId", gameRoom.getWinnerId());
        MessageDto messageDto = MessageDto.builder()
                .type(GomokuMessageType.FINISH_GAME)
                .content(content)
                .build();
        simpMessagingTemplate.convertAndSend(String.format("/topic/gomoku/%s", gameRoomId), messageDto);
        gameRoom.clearGame();
    }

    @Override
    public void turnTimeout(UUID gameRoomId) {
        GameRoom gameRoom = gomokuRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));
        GomokuInstance gameInstance = (GomokuInstance) (gameRoom.getGameInstance()
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_INSTANCE)));

        changeTurn(gameInstance, gameRoomId);
    }

    private void changeTurn(GomokuInstance gameInstance, UUID gameRoomId) {
        Map<String, Object> content = new HashMap<>();
        content.put("id", gameInstance.changeTurnId());

        MessageDto messageDto = MessageDto.builder()
                .type(GomokuMessageType.TURN_CHANGE)
                .content(content)
                .build();
        simpMessagingTemplate.convertAndSend(String.format("/topic/gomoku/%s", gameRoomId), messageDto);
    }
}

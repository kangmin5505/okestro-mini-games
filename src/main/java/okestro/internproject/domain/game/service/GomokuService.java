package okestro.internproject.domain.game.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.dto.gomoku.BoardDto;
import okestro.internproject.domain.game.entity.db.Game;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.entity.memory.GomokuInstance;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.enums.PutResult;
import okestro.internproject.domain.game.exception.GameErrorCode;
import okestro.internproject.domain.game.exception.GameException;
import okestro.internproject.domain.game.repository.stompRepository.GomokuRepository;
import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.domain.user.entity.db.UserGameRecord;
import okestro.internproject.domain.user.service.OnlineUserService;
import okestro.internproject.domain.user.service.UserGameRecordService;
import okestro.internproject.domain.user.service.UserGameStatService;
import okestro.internproject.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class GomokuService extends GameStompService {

    private final GomokuRepository gomokuRepository;
    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final GameService gameService;
    private final UserService userService;
    private final UserGameStatService userGameStatService;
    private final UserGameRecordService userGameRecordService;


    @Autowired
    public GomokuService(GomokuRepository gomokuRepository,
                         OnlineUserService onlineUserService,
                         SimpMessagingTemplate simpMessagingTemplate,
                         ObjectMapper objectMapper,
                         UserGameStatService userGameStatService,
                         GameService gameService,
                         UserService userService,
                         UserGameRecordService userGameRecordService) {
        super(gomokuRepository, onlineUserService, simpMessagingTemplate, objectMapper);
        this.gomokuRepository = gomokuRepository;
        this.onlineUserService = onlineUserService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.objectMapper = objectMapper;
        this.userGameStatService = userGameStatService;
        this.gameService = gameService;
        this.userService = userService;
        this.userGameRecordService = userGameRecordService;
    }

    @Override
    public void startGame(UUID gameRoomId) {
        GameRoom gameRoom = gomokuRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));

        gameRoom.startGame(GameTitle.GOMOKU);
        GomokuInstance gameInstance = (GomokuInstance) gameRoom.getGameInstance();

        BoardDto boardDto = BoardDto.createBoardDto(gameInstance);

        Object boardMessage = objectMapper.createObjectNode()
                .put("type", "INIT_BOARD")
                .set("content", objectMapper.valueToTree(boardDto));
        simpMessagingTemplate.convertAndSend(String.format("/topic/gomoku/%s", gameRoomId), boardMessage);

        changeTurn(gameInstance, gameRoomId);
    }

    public void putStone(UUID gameRoomId, int row, int col) {
        GameRoom gameRoom = gomokuRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));

        GomokuInstance gameInstance = (GomokuInstance) gameRoom.getGameInstance();

        if (gameInstance == null) {
            return;
        }

        PutResult putResult = gameInstance.putStone(row, col);

        Map<String, Object> content = new HashMap<>();
        content.put("row", row);
        content.put("col", col);
        content.put("stoneColor", gameInstance.getCurrentTurnStone().toString());
        content.put("putResult", putResult);

        Object putStoneMessage = objectMapper.createObjectNode()
                .put("type", "PUT_STONE")
                .set("content", objectMapper.valueToTree(content));
        simpMessagingTemplate.convertAndSend(String.format("/topic/gomoku/%s", gameRoomId), putStoneMessage);

        if (putResult == PutResult.FINISH) {
            finishGame(gameRoom, gameRoomId);
        } else if (putResult == PutResult.SUCCESS) {
            changeTurn(gameInstance, gameRoomId);
        }
    }

    @Override
    public void finishGame(GameRoom gameRoom, UUID gameRoomId) {
        gameRoom.finishGame();

        saveResult(gameRoom);

        Map<String, Object> content = new HashMap<>();
        content.put("winnerId", gameRoom.getWinnerId());
        Object finishGameMessage = objectMapper.createObjectNode()
                .put("type", "FINISH_GAME")
                .set("content", objectMapper.valueToTree(content));
        simpMessagingTemplate.convertAndSend(String.format("/topic/gomoku/%s", gameRoomId), finishGameMessage);

        gameRoom.clearGame();
    }


    private void saveResult(GameRoom gameRoom) {
        GomokuInstance gameInstance = (GomokuInstance) gameRoom.getGameInstance();

        Optional<Game> gameOptional = gameService.findByGameTitle(GameTitle.GOMOKU);
        Optional<User> player1 = userService.findById(gameInstance.getPlayer1().getId());
        Optional<User> player2 = userService.findById(gameInstance.getPlayer2().getId());

        if (gameOptional.isPresent() && player1.isPresent() && player2.isPresent()) {
            Game game = gameOptional.get();
            User player1User = player1.get();
            User player2User = player2.get();
            int player1Score = gameInstance.getPlayer1Score();
            int player2Score = gameInstance.getPlayer2Score();
            UUID winnerId = gameInstance.getWinnerId();
            LocalDateTime startTime = gameInstance.getStartTime();
            LocalDateTime finishTime = gameInstance.getFinishTime();

            UserGameRecord userGameRecord = null;
            if (winnerId == player1User.getId()) {
                userGameRecord = UserGameRecord.builder()
                        .game(game)
                        .winUser(player1User)
                        .loseUser(player2User)
                        .winUserScore(player1Score)
                        .loseUserScore(player2Score)
                        .startTime(startTime)
                        .finishTime(finishTime)
                        .build();
                userGameStatService.update(player1User.getId(), GameTitle.CARD_MATCHING, true);
                userGameStatService.update(player2User.getId(), GameTitle.CARD_MATCHING, false);

            } else {
                userGameRecord = UserGameRecord.builder()
                        .game(game)
                        .winUser(player2User)
                        .loseUser(player1User)
                        .winUserScore(player2Score)
                        .loseUserScore(player1Score)
                        .startTime(startTime)
                        .finishTime(finishTime)
                        .build();

                userGameStatService.update(player1User.getId(), GameTitle.CARD_MATCHING, false);
                userGameStatService.update(player2User.getId(), GameTitle.CARD_MATCHING, true);
            }
            userGameRecordService.save(userGameRecord);
        }
    }

    public void turnTimeout(UUID gameRoomId) {
        GameRoom gameRoom = gomokuRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));

        GomokuInstance gameInstance = (GomokuInstance) gameRoom.getGameInstance();
        changeTurn(gameInstance, gameRoomId);
    }

    private void changeTurn(GomokuInstance gameInstance, UUID gameRoomId) {

        Map<String, Object> content = new HashMap<>();
        content.put("id", gameInstance.changeTurnId());

        Object turnMessage = objectMapper.createObjectNode()
                .put("type", "TURN_CHANGE")
                .set("content", objectMapper.valueToTree(content));
        simpMessagingTemplate.convertAndSend(String.format("/topic/gomoku/%s", gameRoomId), turnMessage);
    }
}

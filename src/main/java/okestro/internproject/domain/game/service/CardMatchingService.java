package okestro.internproject.domain.game.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.dto.cardMatching.CardMatchDto;
import okestro.internproject.domain.game.dto.cardMatching.CardsDto;
import okestro.internproject.domain.game.entity.db.Game;
import okestro.internproject.domain.game.entity.memory.CardItem;
import okestro.internproject.domain.game.entity.memory.CardMatchingInstance;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.exception.GameErrorCode;
import okestro.internproject.domain.game.exception.GameException;
import okestro.internproject.domain.game.repository.stompRepository.CardMatchingRepository;
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
import java.util.*;

@Service
@Slf4j
public class CardMatchingService extends GameStompService {

    private final CardMatchingRepository cardMatchingRepository;
    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final UserGameRecordService userGameStatService;
    private final GameService gameService;
    private final UserService userService;
    private final UserGameStatService userGameRecordService;


    @Autowired
    public CardMatchingService(CardMatchingRepository cardMatchingRepository,
                               OnlineUserService onlineUserService,
                               SimpMessagingTemplate simpMessagingTemplate,
                               ObjectMapper objectMapper,
                               UserGameRecordService userGameStatService,
                               GameService gameService,
                               UserService userService,
                               UserGameStatService userGameRecordService) {
        super(cardMatchingRepository, onlineUserService, simpMessagingTemplate, objectMapper);
        this.cardMatchingRepository = cardMatchingRepository;
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
        GameRoom gameRoom = cardMatchingRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));

        gameRoom.startGame(GameTitle.CARD_MATCHING);
        CardMatchingInstance gameInstance = (CardMatchingInstance) gameRoom.getGameInstance();
        List<CardItem> cards = gameInstance.getCards();
        CardsDto cardsDto = CardsDto.createCardsDto(cards);

        Object cardsMessage = objectMapper.createObjectNode()
                .put("type", "INIT_BOARD")
                .set("content", objectMapper.valueToTree(cardsDto));
        simpMessagingTemplate.convertAndSend(String.format("/topic/card-matching/%s", gameRoomId), cardsMessage);

        changeTurn(gameInstance, gameRoomId, false);
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
        simpMessagingTemplate.convertAndSend(String.format("/topic/card-matching/%s", gameRoomId), finishGameMessage);

        gameRoom.clearGame();
    }

    private void saveResult(GameRoom gameRoom) {
        CardMatchingInstance gameInstance = (CardMatchingInstance) gameRoom.getGameInstance();

        Optional<Game> gameOptional = gameService.findByGameTitle(GameTitle.CARD_MATCHING);
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
                userGameRecordService.update(player1User.getId(), GameTitle.CARD_MATCHING, true);
                userGameRecordService.update(player2User.getId(), GameTitle.CARD_MATCHING, false);

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

                userGameRecordService.update(player1User.getId(), GameTitle.CARD_MATCHING, false);
                userGameRecordService.update(player2User.getId(), GameTitle.CARD_MATCHING, true);
            }
            userGameStatService.save(userGameRecord);
        }
    }

    public void checkMatch(UUID gameRoomId, List<CardMatchDto> cardMatchDtos) {

        GameRoom gameRoom = cardMatchingRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));

        CardMatchingInstance gameInstance = (CardMatchingInstance) gameRoom.getGameInstance();

        if (gameInstance.isMatched(cardMatchDtos)) {
            addScore(gameInstance, gameRoomId);
            sendMatchedCardInfo(gameInstance, gameRoomId, cardMatchDtos);
            changeTurn(gameInstance, gameRoomId, true);
            if (gameInstance.isFinished()) {
                finishGame(gameRoom, gameRoomId);
            }
        } else {
            changeTurn(gameInstance, gameRoomId, false);
        }
    }

    public void turnTimeout(UUID gameRoomId) {
        GameRoom gameRoom = cardMatchingRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));

        CardMatchingInstance gameInstance = (CardMatchingInstance) gameRoom.getGameInstance();
        changeTurn(gameInstance, gameRoomId, false);
    }


    private void sendMatchedCardInfo(CardMatchingInstance gameInstance, UUID gameRoomId, List<CardMatchDto> cardMatchDtos) {
        CardMatchDto cardMatchDto1 = cardMatchDtos.get(0);
        CardMatchDto cardMatchDto2 = cardMatchDtos.get(1);

        int position1 = cardMatchDto1.getPosition();
        int position2 = cardMatchDto2.getPosition();

        Map<String, Object> matchedCardContent = new HashMap<>();
        matchedCardContent.put("id", gameInstance.getCurrentTurnId());
        matchedCardContent.put("position1", position1);
        matchedCardContent.put("position2", position2);

        Object matchedCardMessage = objectMapper.createObjectNode()
                .put("type", "MATCHED_CARD")
                .set("content", objectMapper.valueToTree(matchedCardContent));

        simpMessagingTemplate.convertAndSend(String.format("/topic/card-matching/%s", gameRoomId), matchedCardMessage);
    }

    private void addScore(CardMatchingInstance gameInstance, UUID gameRoomId) {
        gameInstance.addScore();

        Map<String, Object> scoreChangeContent = new HashMap<>();
        scoreChangeContent.put("player1Score", gameInstance.getPlayer1Score());
        scoreChangeContent.put("player2Score", gameInstance.getPlayer2Score());

        Object scoreChangeMessage = objectMapper.createObjectNode()
                .put("type", "SCORE_CHANGE")
                .set("content", objectMapper.valueToTree(scoreChangeContent));

        simpMessagingTemplate.convertAndSend(String.format("/topic/card-matching/%s", gameRoomId), scoreChangeMessage);

    }

    private void changeTurn(CardMatchingInstance gameInstance, UUID gameRoomId, boolean isMatched) {

        Map<String, Object> content = new HashMap<>();
        if (isMatched) {
            content.put("id", gameInstance.getCurrentTurnId());
        } else {
            content.put("id", gameInstance.changeTurnId());
        }
        Object turnChangeMessage = objectMapper.createObjectNode()
                .put("type", "TURN_CHANGE")
                .set("content", objectMapper.valueToTree(content));
        simpMessagingTemplate.convertAndSend(String.format("/topic/card-matching/%s", gameRoomId), turnChangeMessage);
    }
}

package okestro.internproject.domain.game.service;

import io.micrometer.core.annotation.Counted;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.dto.cardMatching.CardMatchDtos;
import okestro.internproject.domain.game.dto.cardMatching.CardsDto;
import okestro.internproject.domain.game.dto.common.MessageDto;
import okestro.internproject.domain.game.entity.memory.CardItem;
import okestro.internproject.domain.game.entity.memory.CardMatchingInstance;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.enums.CardMatchingMessageType;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.exception.GameErrorCode;
import okestro.internproject.domain.game.exception.GameException;
import okestro.internproject.domain.game.repository.stompRepository.CardMatchingRepository;
import okestro.internproject.domain.user.service.OnlineUserService;
import okestro.internproject.domain.user.service.UserGameRecordService;
import okestro.internproject.domain.user.service.UserGameStatService;
import okestro.internproject.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class CardMatchingService extends GameStompService {

    private final CardMatchingRepository cardMatchingRepository;


    @Autowired
    public CardMatchingService(CardMatchingRepository cardMatchingRepository,
                               OnlineUserService onlineUserService,
                               SimpMessagingTemplate simpMessagingTemplate,
                               UserGameRecordService userGameRecordService,
                               GameService gameService,
                               UserService userService,
                               UserGameStatService userGameStatService) {
        super(cardMatchingRepository, onlineUserService, simpMessagingTemplate,
                gameService, userService, userGameRecordService, userGameStatService);
        this.cardMatchingRepository = cardMatchingRepository;
    }

    @Override
    @Counted("my.game.card-matching.start")
    public void startGame(UUID gameRoomId) {
        GameRoom gameRoom = cardMatchingRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));
        CardMatchingInstance gameInstance = (CardMatchingInstance) (gameRoom.startGame(GameTitle.CARD_MATCHING)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM)));

        List<CardItem> cards = gameInstance.getCards();

        MessageDto messageDto = MessageDto.builder()
                .type(CardMatchingMessageType.INIT_BOARD)
                .content(CardsDto.createCardsDto(cards))
                .build();
        simpMessagingTemplate.convertAndSend(String.format("/topic/card-matching/%s", gameRoomId), messageDto);
        changeTurn(gameInstance, gameRoomId, false);
    }

    @Override
    public void finishGame(GameRoom gameRoom, UUID gameRoomId) {
        gameRoom.finishGame();
        CardMatchingInstance gameInstance = (CardMatchingInstance) (gameRoom.getGameInstance()
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_INSTANCE)));
        saveResult(gameInstance, GameTitle.CARD_MATCHING);

        Map<String, Object> content = new HashMap<>();
        content.put("winnerId", gameRoom.getWinnerId());

        MessageDto messageDto = MessageDto.builder()
                .type(CardMatchingMessageType.FINISH_GAME)
                .content(content)
                .build();

        simpMessagingTemplate.convertAndSend(String.format("/topic/card-matching/%s", gameRoomId), messageDto);
        gameRoom.clearGame();
    }

    public void checkMatch(UUID gameRoomId, CardMatchDtos cardMatchDtos) {
        GameRoom gameRoom = cardMatchingRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));
        CardMatchingInstance gameInstance = (CardMatchingInstance) (gameRoom.getGameInstance()
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM)));

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

    @Override
    public void turnTimeout(UUID gameRoomId) {
        GameRoom gameRoom = cardMatchingRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));
        CardMatchingInstance gameInstance = (CardMatchingInstance) (gameRoom.getGameInstance()
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM)));

        changeTurn(gameInstance, gameRoomId, false);
    }


    private void sendMatchedCardInfo(CardMatchingInstance gameInstance, UUID gameRoomId, CardMatchDtos cardMatchDtos) {
        int position1 = cardMatchDtos.getCard1().getPosition();
        int position2 = cardMatchDtos.getCard2().getPosition();

        Map<String, Object> content = new HashMap<>();
        content.put("id", gameInstance.getCurrentTurnId());
        content.put("position1", position1);
        content.put("position2", position2);

        MessageDto messageDto = MessageDto.builder()
                .type(CardMatchingMessageType.MATCHED_CARD)
                .content(content)
                .build();

        simpMessagingTemplate.convertAndSend(String.format("/topic/card-matching/%s", gameRoomId), messageDto);
    }

    private void addScore(CardMatchingInstance gameInstance, UUID gameRoomId) {
        gameInstance.addScore();

        Map<String, Object> content = new HashMap<>();
        content.put("player1Score", gameInstance.getPlayer1Score());
        content.put("player2Score", gameInstance.getPlayer2Score());

        MessageDto messageDto = MessageDto.builder()
                .type(CardMatchingMessageType.SCORE_CHANGE)
                .content(content)
                .build();

        simpMessagingTemplate.convertAndSend(String.format("/topic/card-matching/%s", gameRoomId), messageDto);

    }

    private void changeTurn(CardMatchingInstance gameInstance, UUID gameRoomId, boolean isMatched) {
        Map<String, Object> content = new HashMap<>();
        content.put("id", isMatched ? gameInstance.getCurrentTurnId() : gameInstance.changeTurnId());

        MessageDto messageDto = MessageDto.builder()
                .type(CardMatchingMessageType.TURN_CHANGE)
                .content(content)
                .build();

        simpMessagingTemplate.convertAndSend(String.format("/topic/card-matching/%s", gameRoomId), messageDto);
    }
}

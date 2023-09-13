package okestro.internproject.domain.game.service;

import lombok.RequiredArgsConstructor;
import okestro.internproject.domain.game.dto.GameRoomCreateDto;
import okestro.internproject.domain.game.dto.common.MessageDto;
import okestro.internproject.domain.game.entity.db.Game;
import okestro.internproject.domain.game.entity.memory.GameInstance;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.entity.memory.GameRoomInfo;
import okestro.internproject.domain.game.enums.GameMessageType;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.exception.GameErrorCode;
import okestro.internproject.domain.game.exception.GameException;
import okestro.internproject.domain.game.repository.stompRepository.StompRepository;
import okestro.internproject.domain.user.entity.SimpleUser;
import okestro.internproject.domain.user.entity.db.User;
import okestro.internproject.domain.user.entity.db.UserGameRecord;
import okestro.internproject.domain.user.entity.memory.OnlineUser;
import okestro.internproject.domain.user.exception.UserErrorCode;
import okestro.internproject.domain.user.exception.UserException;
import okestro.internproject.domain.user.service.OnlineUserService;
import okestro.internproject.domain.user.service.UserGameRecordService;
import okestro.internproject.domain.user.service.UserGameStatService;
import okestro.internproject.domain.user.service.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
public abstract class GameStompService {

    private final StompRepository stompRepository;
    protected final OnlineUserService onlineUserService;
    protected final SimpMessagingTemplate simpMessagingTemplate;
    protected final GameService gameService;
    protected final UserService userService;
    protected final UserGameRecordService userGameRecordService;
    protected final UserGameStatService userGameStatService;


    public abstract void startGame(UUID gameRoomId);

    public abstract void finishGame(GameRoom gameRoom, UUID gameRoomId);

    public abstract void turnTimeout(UUID gameRoomId);

    public List<GameRoom> getAllGameRoom() {
        return stompRepository.findAll();
    }

    protected void saveResult(GameInstance gameInstance, GameTitle gameTitle) {
        Game game = gameService.findByGameTitle(gameTitle)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME));
        User player1 = userService.findById(gameInstance.getPlayer1().getId())
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));
        User player2 = userService.findById(gameInstance.getPlayer2().getId())
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));

        int player1Score = gameInstance.getPlayer1Score();
        int player2Score = gameInstance.getPlayer2Score();
        UUID winnerId = gameInstance.getWinnerId();
        LocalDateTime startTime = gameInstance.getStartTime();
        LocalDateTime finishTime = gameInstance.getFinishTime();

        UserGameRecord userGameRecord = UserGameRecord.builder()
                .game(game)
                .winUser(winnerId == player1.getId() ? player1 : player2)
                .loseUser(winnerId == player1.getId() ? player2 : player1)
                .winUserScore(winnerId == player1.getId() ? player1Score : player2Score)
                .loseUserScore(winnerId == player1.getId() ? player2Score : player1Score)
                .startTime(startTime)
                .finishTime(finishTime)
                .build();
        userGameStatService.update(player1.getId(), gameTitle, winnerId == player1.getId());
        userGameStatService.update(player2.getId(), gameTitle, winnerId == player2.getId());
        userGameRecordService.save(userGameRecord);
    }

    public GameRoom createGameRoom(String gameTitle, GameRoomCreateDto gameRoomCreateDto, SimpleUser user) {
        if (onlineUserService.isInGameRoom(user.getId())) {
            throw new GameException(GameErrorCode.ALREADY_JOIN_GAME_ROOM);
        }
        GameRoom gameRoom = GameRoom.builder()
                .gameRoomInfo(GameRoomInfo.createGameRoomInfo(gameRoomCreateDto.getTitle(), user))
                .gameInstance(null)
                .build();
        stompRepository.save(gameRoom);
        onlineUserService.joinGameRoom(user, gameTitle, gameRoom.getGameRoomInfo().getId());
        return gameRoom;
    }

    public Optional<GameRoom> joinGameRoom(String gameTitle, UUID gameRoomId, SimpleUser user) {
        GameRoom gameRoom = stompRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));

        if (onlineUserService.isInGameRoom(user.getId())) {
            throw new GameException(GameErrorCode.ALREADY_JOIN_GAME_ROOM);
        }
        if (!gameRoom.getGameRoomInfo().isCanJoin()) {
            throw new GameException((GameErrorCode.CAN_NOT_JOIN_GAME_ROOM));
        }

        onlineUserService.joinGameRoom(user, gameTitle, gameRoom.getGameRoomInfo().getId());

        return Optional.of(gameRoom)
                .map(GameRoom::getGameRoomInfo)
                .map(gameRoomInfo -> {
                    gameRoomInfo.joinUser(user);
                    sendJoinGameRoomMessage(gameTitle, gameRoomId, user);
                    sendJoinMaintainMessage(gameTitle, gameRoomId, user);
                    return Optional.of(gameRoom);
                })
                .orElse(Optional.empty());
    }

    private void sendJoinMaintainMessage(String gameTitle, UUID gameRoomId, SimpleUser user) {
        MessageDto messageDto = MessageDto.builder()
                .type(GameMessageType.JOIN)
                .content(user)
                .build();
        simpMessagingTemplate.convertAndSend(String.format("/topic/room-maintain/%s/%s", gameTitle, gameRoomId), messageDto);
    }

    private void sendJoinGameRoomMessage(String gameTitle, UUID gameRoomId, SimpleUser user) {
        MessageDto messageDto = MessageDto.builder()
                .type(GameMessageType.JOIN)
                .content(String.format("%s님이 입장하셨습니다.", user.getNickname()))
                .build();
        simpMessagingTemplate.convertAndSend(String.format("/topic/chat/%s/%s", gameTitle, gameRoomId), messageDto);
    }

    public Optional<GameRoom> findById(UUID gameRoomId) {
        return stompRepository.findById(gameRoomId);
    }

    public void deleteGameRoom(GameTitle gameTitle, UUID gameRoomId, SimpleUser user) {
        GameRoom gameRoom = stompRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));
        GameRoomInfo gameRoomInfo = gameRoom.getGameRoomInfo();
        gameRoom.getGameInstance()
                .ifPresent(gameInstance -> {
                    if (gameRoomInfo.isOnGame()) {
                        gameInstance.leaveUserOnGame(user.getId());
                        finishGame(gameRoom, gameRoomId);
                    }
                });

        UUID hostId = gameRoomInfo.getHostId();
        UUID userId = user.getId();
        if (hostId.equals(userId)) {
            MessageDto messageDto = MessageDto.builder()
                    .type(GameMessageType.DESTROY)
                    .build();

            if (gameRoomInfo.getPlayer2() != null) {
                OnlineUser onlineUser = onlineUserService.findByUserId(gameRoomInfo.getPlayer2().getId())
                        .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));
                onlineUserService.exitGameRoom(onlineUser.getUser().getId());
            }
            simpMessagingTemplate.convertAndSend(String.format("/topic/room-maintain/%s/%s", gameTitle.getTitle(), gameRoomId), messageDto);
            stompRepository.deleteById(gameRoomId);
        } else {
            gameRoomInfo.exitUser();
            sendLeaveMaintainMessage(gameTitle.getTitle(), gameRoomId, user);
            sendLeaveRoomMessage(gameTitle.getTitle(), gameRoomId, user);
        }

        OnlineUser onlineUser = onlineUserService.findByUserId(user.getId())
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));
        onlineUserService.exitGameRoom(onlineUser.getUser().getId());
    }

    private void sendLeaveMaintainMessage(String gameTitle, UUID gameRoomId, SimpleUser user) {
        Map<String, Object> content = new HashMap<>();
        content.put("id", user.getId());

        MessageDto messageDto = MessageDto.builder()
                .type(GameMessageType.LEAVE)
                .content(content)
                .build();
        simpMessagingTemplate.convertAndSend(String.format("/topic/room-maintain/%s/%s", gameTitle, gameRoomId), messageDto);
    }

    private void sendLeaveRoomMessage(String gameTitle, UUID gameRoomId, SimpleUser user) {
        MessageDto messageDto = MessageDto.builder()
                .type(GameMessageType.LEAVE)
                .content(String.format("%s님이 퇴장하셨습니다.", user.getNickname()))
                .build();
        simpMessagingTemplate.convertAndSend(String.format("/topic/chat/%s/%s", gameTitle, gameRoomId), messageDto);
    }

    public boolean isCanStartGame(UUID gameRoomId, SimpleUser user) {
        GameRoom gameRoom = stompRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));

        GameRoomInfo gameRoomInfo = gameRoom.getGameRoomInfo();
        if (!gameRoomInfo.isHost(user))
            throw new GameException(GameErrorCode.NOT_GAME_HOST);
        if (!gameRoomInfo.isEnoughPlayers())
            throw new GameException(GameErrorCode.NOT_ENOUGH_PLAYERS);
        if (!gameRoomInfo.isReadyAllPlayers())
            throw new GameException(GameErrorCode.NOT_READY_ALL_PLAYERS);
        if (gameRoomInfo.isOnGame())
            throw new GameException(GameErrorCode.ALREADY_START_GAME);
        return true;
    }

}

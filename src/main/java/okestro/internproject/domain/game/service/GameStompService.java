package okestro.internproject.domain.game.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.game.dto.GameRoomCreateDto;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.entity.memory.GameRoomInfo;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.enums.State;
import okestro.internproject.domain.game.exception.GameErrorCode;
import okestro.internproject.domain.game.exception.GameException;
import okestro.internproject.domain.game.repository.stompRepository.StompRepository;
import okestro.internproject.domain.user.entity.SimpleUser;
import okestro.internproject.domain.user.entity.memory.OnlineUser;
import okestro.internproject.domain.user.exception.UserErrorCode;
import okestro.internproject.domain.user.exception.UserException;
import okestro.internproject.domain.user.service.OnlineUserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public abstract class GameStompService {

    private final StompRepository stompRepository;
    private final OnlineUserService onlineUserService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ObjectMapper objectMapper;

    public abstract void startGame(UUID gameRoomId);

    public abstract void finishGame(GameRoom gameRoom, UUID gameRoomId);

    public List<GameRoom> getAllGameRoom() {
        return stompRepository.findAll();
    }

    public GameRoom createGameRoom(String gameTitle, GameRoomCreateDto gameRoomCreateDto, SimpleUser user) {
        if (onlineUserService.isInGameRoom(user.getId())) {
            throw new GameException(GameErrorCode.ALREADY_JOIN_GAME_ROOM);
        }
        GameRoom gameRoom = GameRoom.builder()
                .gameRoomInfo(GameRoomInfo.builder()
                        .id(UUID.randomUUID())
                        .title(gameRoomCreateDto.getTitle())
                        .userNum(1L)
                        .limitUserNum(2L)
                        .state(State.WAITING)
                        .hostId(user.getId())
                        .player1(user)
                        .player2(null)
                        .build())
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

        JsonNode joinMessage = objectMapper.createObjectNode()
                .put("type", "JOIN")
                .putPOJO("content", user);
        simpMessagingTemplate.convertAndSend(String.format("/topic/room-maintain/%s/%s", gameTitle, gameRoomId), joinMessage);
    }

    private void sendJoinGameRoomMessage(String gameTitle, UUID gameRoomId, SimpleUser user) {
        JsonNode joinMessage = objectMapper.createObjectNode()
                .put("type", "JOIN")
                .put("content", String.format("%s님이 입장하셨습니다.", user.getNickname()));
        simpMessagingTemplate.convertAndSend(String.format("/topic/chat/%s/%s", gameTitle, gameRoomId), joinMessage);
    }

    public Optional<GameRoom> findById(UUID gameRoomId) {
        return stompRepository.findById(gameRoomId);
    }

    public void deleteGameRoom(GameTitle gameTitle, UUID gameRoomId, SimpleUser user) {
        GameRoom gameRoom = stompRepository.findById(gameRoomId)
                .orElseThrow(() -> new GameException(GameErrorCode.NOT_EXIST_GAME_ROOM));

        if (gameRoom.getGameRoomInfo().isOnGame()) {
            gameRoom.getGameInstance().leaveUserOnGame(user.getId());
            finishGame(gameRoom, gameRoomId);
        }

        UUID hostId = gameRoom.getGameRoomInfo().getHostId();
        UUID userId = user.getId();

        if (hostId.equals(userId)) {
            JsonNode destroyJson = objectMapper.createObjectNode()
                    .put("type", "DESTROY");

            if (gameRoom.getGameRoomInfo().getPlayer2() != null) {
                OnlineUser onlineUser = onlineUserService.findByUserId(gameRoom.getGameRoomInfo().getPlayer2().getId())
                        .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));
                onlineUserService.exitGameRoom(onlineUser.getUser().getId());
            }

            simpMessagingTemplate.convertAndSend(String.format("/topic/room-maintain/%s/%s", gameTitle.getTitle(), gameRoomId), destroyJson);
            stompRepository.deleteById(gameRoomId);
        } else {
            gameRoom.getGameRoomInfo().exitUser();

            sendLeaveMaintainMessage(gameTitle.getTitle(), gameRoomId, user);
            sendLeaveRoomMessage(gameTitle.getTitle(), gameRoomId, user);
        }


        OnlineUser onlineUser = onlineUserService.findByUserId(user.getId())
                .orElseThrow(() -> new UserException(UserErrorCode.CAN_NOT_FIND_USER));
        onlineUserService.exitGameRoom(onlineUser.getUser().getId());
    }

    private void sendLeaveMaintainMessage(String gameTitle, UUID gameRoomId, SimpleUser user) {
        ObjectNode contentJson = objectMapper.createObjectNode()
                .put("id", user.getId().toString());

        JsonNode leaveJson = objectMapper.createObjectNode()
                .put("type", "LEAVE")
                .set("content", contentJson);

        simpMessagingTemplate.convertAndSend(String.format("/topic/room-maintain/%s/%s", gameTitle, gameRoomId), leaveJson);
    }

    private void sendLeaveRoomMessage(String gameTitle, UUID gameRoomId, SimpleUser user) {
        JsonNode leaveMessage = objectMapper.createObjectNode()
                .put("type", "LEAVE")
                .put("content", String.format("%s님이 퇴장하셨습니다.", user.getNickname()));
        simpMessagingTemplate.convertAndSend(String.format("/topic/chat/%s/%s", gameTitle, gameRoomId), leaveMessage);
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

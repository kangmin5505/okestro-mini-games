package okestro.internproject.domain.game.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okestro.internproject.domain.auth.jwt.provider.JwtProvider;
import okestro.internproject.domain.game.dto.GameRoomCreateDto;
import okestro.internproject.domain.game.dto.GameRoomInfoDto;
import okestro.internproject.domain.game.dto.GameRoomInfosDto;
import okestro.internproject.domain.game.dto.GamesDto;
import okestro.internproject.domain.game.entity.memory.GameRoom;
import okestro.internproject.domain.game.enums.GameTitle;
import okestro.internproject.domain.game.exception.GameErrorCode;
import okestro.internproject.domain.game.exception.GameException;
import okestro.internproject.domain.game.service.CardMatchingService;
import okestro.internproject.domain.game.service.GameService;
import okestro.internproject.domain.game.service.GameStompService;
import okestro.internproject.domain.user.entity.SimpleUser;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@Slf4j
public class GameController {

    private final GameService gameService;
    private final CardMatchingService cardMatchingService;
    private final Map<GameTitle, GameStompService> gameStompServices = new ConcurrentHashMap<>();


    @PostConstruct
    public void init() {
        gameStompServices.put(GameTitle.CARD_MATCHING, cardMatchingService);
    }

    @GetMapping
    public GamesDto getGames() {
        return GamesDto.createGamesDto(gameService.findAllGame());
    }

    @GetMapping("/{gameTitle}")
    public GameRoomInfosDto getGameRooms(@PathVariable GameTitle gameTitle) {
        List<GameRoom> gameRoomList = gameStompServices.get(gameTitle).getAllGameRoom();
        return GameRoomInfosDto.createGameRoomInfosDto(gameTitle.getTitle(), gameRoomList);
    }

    @PostMapping("/{gameTitle}")
    public GameRoomInfoDto createGameRoom(@PathVariable GameTitle gameTitle,
                                          @RequestBody @Valid GameRoomCreateDto gameRoomCreateDto,
                                          @CookieValue(JwtProvider.TYPE_ACCESS) SimpleUser user) {
        GameRoom gameRoom = gameStompServices.get(gameTitle)
                .createGameRoom(gameTitle.getTitle(), gameRoomCreateDto, user);
        return GameRoomInfoDto.createGameRoomInfoDto(gameRoom);
    }


    @GetMapping("/{gameTitle}/{gameRoomId}")
    public GameRoomInfoDto joinGameRoom(@PathVariable GameTitle gameTitle, @PathVariable UUID gameRoomId,
                                        @CookieValue(JwtProvider.TYPE_ACCESS) SimpleUser user) {
        return gameStompServices.get(gameTitle).joinGameRoom(gameTitle.getTitle(), gameRoomId, user)
                .map(GameRoomInfoDto::createGameRoomInfoDto)
                .orElseThrow(() -> new GameException(GameErrorCode.CAN_NOT_JOIN_GAME_ROOM));

    }

    @DeleteMapping("/{gameTitle}/{gameRoomId}")
    public void deleteGameRoom(@PathVariable GameTitle gameTitle, @PathVariable UUID gameRoomId,
                               @CookieValue(JwtProvider.TYPE_ACCESS) SimpleUser user) {
        gameStompServices.get(gameTitle).deleteGameRoom(gameRoomId, user);
    }

    @GetMapping("/{gameTitle}/{gameRoomId}/start")
    public void startGame(@PathVariable GameTitle gameTitle, @PathVariable UUID gameRoomId,
                          @CookieValue(JwtProvider.TYPE_ACCESS) SimpleUser user) {
        GameStompService gameStompService = gameStompServices.get(gameTitle);
        if (gameStompService.isCanStartGame(gameRoomId, user)) {
            gameStompService.startGame(gameRoomId);
        }
    }
}

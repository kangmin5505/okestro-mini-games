package okestro.internproject.domain.game.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import okestro.internproject.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GameErrorCode implements ErrorCode {

    CAN_NOT_JOIN_GAME_ROOM(HttpStatus.BAD_REQUEST, "방에 입장하실 수 없습니다."),
    CAN_NOT_CREATE_GAME_ROOM(HttpStatus.BAD_REQUEST, "방을 생성하실 수 없습니다."),
    NOT_GAME_HOST(HttpStatus.UNAUTHORIZED, "게임 시작 권한이 없습니다."),
    NOT_READY_ALL_PLAYERS(HttpStatus.BAD_REQUEST, "모든 플레이어가 게임 준비를 해야 합니다."),
    NOT_ENOUGH_PLAYERS(HttpStatus.BAD_REQUEST, "플레이어가 부족합니다."),
    ALREADY_START_GAME(HttpStatus.BAD_REQUEST, "이미 게임이 시작되었습니다."),
    NOT_EXIST_GAME_ROOM(HttpStatus.BAD_REQUEST, "존재하지 않는 방입니다."),
    ALREADY_JOIN_GAME_ROOM(HttpStatus.BAD_REQUEST, "이미 게임방에 참여중입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}

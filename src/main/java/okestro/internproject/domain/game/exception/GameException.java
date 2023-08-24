package okestro.internproject.domain.game.exception;

import okestro.internproject.global.exception.ErrorCode;
import okestro.internproject.global.exception.GlobalException;



public class GameException extends GlobalException {

    public GameException(ErrorCode errorCode) {
        super(errorCode);
    }
}

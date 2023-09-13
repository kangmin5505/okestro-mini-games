package okestro.internproject.domain.user.exception;

import okestro.internproject.global.exception.ErrorCode;
import okestro.internproject.global.exception.GlobalException;

public class UserException extends GlobalException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}

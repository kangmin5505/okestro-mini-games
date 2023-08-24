package okestro.internproject.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;
}

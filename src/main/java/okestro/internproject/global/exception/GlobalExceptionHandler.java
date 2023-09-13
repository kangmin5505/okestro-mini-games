package okestro.internproject.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ErrorResponse handleException(GlobalException e, HttpServletResponse response) {
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        ErrorCode errorCode = e.getErrorCode();

        return ErrorResponse.builder()
                .statusCode(errorCode.getHttpStatus().value())
                .message(errorCode.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentNotValidException e, HttpServletResponse response) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletResponse response) {
        if (e.getRequiredType() != null && e.getRequiredType().getSimpleName().equals("SimpleUser")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            return ErrorResponse.builder()
                    .statusCode(HttpStatus.UNAUTHORIZED.value())
                    .message("만료된 토큰입니다.")
                    .build();
        }

        response.setStatus(HttpStatus.BAD_REQUEST.value());
        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("잘못된 요청입니다.")
                .build();
    }
}

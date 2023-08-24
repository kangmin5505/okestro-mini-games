package okestro.internproject.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ErrorResponse handleException(GlobalException e, HttpServletResponse response) {
        response.setStatus(e.getErrorCode().getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

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
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();

    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletResponse response) {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        return ErrorResponse.builder()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .message("잘못된 요청입니다.")
                .build();

    }
}

package ru.practicum.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.dto.ApiError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.text.Format;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                          HttpServletRequest request) {
        log.warn("Ошибка валидации полей объекта: {} \nПуть запроса: {}",
                Objects.requireNonNull(e.getFieldError()).getDefaultMessage(), request.getServletPath());
        String message = String.format("Field: %s. Error: %s. Value: %s",
                e.getFieldError().getField(), e.getFieldError().getDefaultMessage(), e.getCause());
        ApiError er = new ApiError(HttpStatus.BAD_REQUEST.name(), "Incorrectly made request.", message,
                formatter.format(LocalDateTime.now()));
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerIllegalArgumentException(IllegalArgumentException e,
                                                               HttpServletRequest request) {
        log.warn("Ошибка валидации запроса: {} \nПуть запроса: {}", e.getMessage(), request.getServletPath());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handlerConstraintViolationException(ConstraintViolationException e,
                                                                        HttpServletRequest request) {
        log.warn("Ошибка валидации запроса: {} \nПуть запроса: {}", e.getMessage(), request.getServletPath());
        /*return new ResponseEntity<>(e.getMessage() + "\nПуть запроса: "
                + request.getServletPath(), HttpStatus.BAD_REQUEST);*/
        ApiError er = new ApiError("CONFLICT", "Integrity constraint has been violated.",
                e.getMessage(), LocalDateTime.now().toString());
        return new ResponseEntity<>(er, HttpStatus.CONFLICT);
    }



    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e, HttpServletRequest request) {
        log.warn("Произошла непредвиденная ошибка: {} \nПуть запроса: {}", e.getMessage(), request.getServletPath());
        return new ErrorResponse("Произошла непредвиденная ошибка по пути запроса: " + request.getServletPath());
    }
}



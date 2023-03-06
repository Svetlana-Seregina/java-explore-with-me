package ru.practicum.explorewithme.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerIllegalArgumentException(IllegalArgumentException e,
                                                               HttpServletRequest request) {
        log.warn("Ошибка валидации запроса: {} \nПуть запроса: {}", e.getMessage(), request.getServletPath());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handlerConstraintViolationException(ConstraintViolationException e,
                                                                      HttpServletRequest request) {
        log.warn("Ошибка валидации запроса: {} \nПуть запроса: {}", e.getMessage(), request.getServletPath());
        return new ResponseEntity<>(e.getMessage() + "\nПуть запроса: "
                + request.getServletPath(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                        HttpServletRequest request) {
        log.warn("Ошибка валидации полей объекта: {} \nПуть запроса: {}",
                e.getFieldError().getDefaultMessage(), request.getServletPath());
        return new ResponseEntity<>(e.getFieldError().getDefaultMessage()
                + "\nПуть запроса: " + request.getServletPath(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e, HttpServletRequest request) {
        log.warn("Произошла непредвиденная ошибка: {} \nПуть запроса: {}", e.getMessage(), request.getServletPath());
        return new ErrorResponse("Произошла непредвиденная ошибка по пути запроса: " + request.getServletPath());
    }
}

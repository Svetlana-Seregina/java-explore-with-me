package ru.practicum.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explorewithme.dto.ApiError;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                                          HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + " -> " + error.getDefaultMessage());
        }

        log.warn("Ошибка валидации полей объекта: {} \nПуть запроса: {}",
                Objects.requireNonNull(e.getFieldError()).getDefaultMessage(), request.getServletPath());

        String message = String.format("Field: %s. Error: %s. Value: %s",
                e.getFieldError().getField(), e.getFieldError().getDefaultMessage(), e.getCause());

        ApiError apiError = new ApiError(errors, HttpStatus.BAD_REQUEST, "Incorrectly made request.", message,
                LocalDateTime.now());

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<ApiError> handleThrowable(final Throwable e, HttpServletRequest request) {
        log.warn("Произошла непредвиденная ошибка: {} \nПуть запроса: {}", e.getMessage(), request.getServletPath());
        ApiError apiError = new ApiError(e.toString(), HttpStatus.CONFLICT, "Integrity constraint has been violated.",
                e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handleMissingServletRequestParameterException(MissingServletRequestParameterException e,
                                                                                  HttpServletRequest request) {
        log.warn("Отсутствует требуемый параметр запроса: {} \nПуть запроса: {}", e.getMessage(), request.getServletPath());
        ApiError apiError = new ApiError(e.toString(), HttpStatus.BAD_REQUEST, "Incorrectly made request.",
                e.getMessage(), LocalDateTime.now());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ApiError> handlerConstraintViolationException(ConstraintViolationException e,
                                                                        HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": "
                    + violation.getMessage());
        }
        log.warn("Ошибка валидации запроса: {} \nПуть запроса: {}", e.getMessage(), request.getServletPath());
        String message = String.format("Field: %s. Error: %s. Value: %s",
                e.getConstraintViolations(), e.getMessage(), e.getCause());
        ApiError er = new ApiError(errors, HttpStatus.BAD_REQUEST, "Integrity constraint has been violated.",
                message, LocalDateTime.now());
        return new ResponseEntity<>(er, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerIllegalArgumentException(IllegalArgumentException e,
                                                               HttpServletRequest request) {
        log.warn("Ошибка валидации запроса: {} \nПуть запроса: {}", e.getMessage(), request.getServletPath());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException e,
                                                                  HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

        log.warn("Ошибка: объект не найден в базе: {} \nПуть запроса: {}",
                e.getMessage(), request.getServletPath());

        String message = String.format("StackTrace: %s. Error: %s. Value: %s",
                Arrays.toString(e.getStackTrace()), e.getMessage(), e.getCause());

        ApiError apiError = new ApiError(errors, HttpStatus.NOT_FOUND, "Incorrectly made request.", message,
                LocalDateTime.now());

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

}



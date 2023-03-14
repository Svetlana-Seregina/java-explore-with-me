package ru.practicum.explorewithme.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ApiError {
    // Сведения об ошибке
    //private final String status; // Код статуса HTTP-ответа
    //private final String reason; // Общее описание причины ошибки
    //private final String message; // Сообщение об ошибке
    //private final String timestamp; // Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")
    //private final String errors; // Список стектрейсов или описания ошибок

    private final Object errors;
    private final HttpStatus status;
    private final String reason;
    private final String message;
    //@JsonDeserialize()
    private final String timestamp;

}

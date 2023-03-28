package ru.practicum.explorewithme.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    // Сведения об ошибке

    private Object errors; // Список стектрейсов или описания ошибок

    private HttpStatus status; // Код статуса HTTP-ответа

    private String reason; // Общее описание причины ошибки

    private String message; // Сообщение об ошибке

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp; // Дата и время когда произошла ошибка (в формате "yyyy-MM-dd HH:mm:ss")

}

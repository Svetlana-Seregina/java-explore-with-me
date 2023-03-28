package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category; // id категории к которой относится событие

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description; // полное описание события

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"

    private Location location;

    private Boolean paid; // default: false; Нужно ли оплачивать участие в событии

    private Long participantLimit; // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения. default: 0

    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие.
    // Если true, то все заявки будут ожидать подтверждения инициатором события.
    // Если false - то будут подтверждаться автоматически.

    @Size(min = 3, max = 120)
    private String title; // Заголовок события

    @Data
    public static class Location {
        private final double lat;
        private final double lon;
    }

}

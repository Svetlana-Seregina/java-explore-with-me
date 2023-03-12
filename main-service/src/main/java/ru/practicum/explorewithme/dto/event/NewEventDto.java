package ru.practicum.explorewithme.dto.event;

import lombok.Data;
import ru.practicum.explorewithme.dto.Location;

import java.time.LocalDateTime;

@Data
public class NewEventDto {
    // Новое событие

    private final String annotation; // Краткое описание события: maxLength: 2000; minLength: 20;
    private final Long category; // id категории к которой относится событие
    private final String description; // полное описание события: maxLength: 7000; minLength: 20;
    private final LocalDateTime eventDate; // Дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    private final Location location;
    private final Boolean paid; // default: false; Нужно ли оплачивать участие в событии
    private final Long participantLimit; // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения. default: 0
    private final Boolean requestModeration; // Нужна ли пре-модерация заявок на участие.
    // Если true, то все заявки будут ожидать подтверждения инициатором события.
    // Если false - то будут подтверждаться автоматически.
    private final String title; // Заголовок события: maxLength: 120; minLength: 3;

}

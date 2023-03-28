package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventFullDto {

    private final String annotation;

    private final CategoryDto category;

    private final Long confirmedRequests; // Количество одобренных заявок на участие в данном событии

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdOn; // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")

    private final String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")

    private final Long id; // Идентификатор

    private final UserShortDto initiator;

    private final Location location;

    private final Boolean paid; // Нужно ли оплачивать участие

    private final Long participantLimit; // Ограничение на количество участников. Значение 0 - означает отсутствие ограничения

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime publishedOn; // Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss")

    private final Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

    private final EventState state; // Список состояний жизненного цикла события: PENDING, PUBLISHED, CANCELED

    private final String title; // Заголовок

    private final Long views; // Количество просмотрев события

    @Data
    public static class Location {
        private final double lat;
        private final double lon;
    }

}

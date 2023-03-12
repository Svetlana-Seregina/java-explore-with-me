package ru.practicum.explorewithme.dto.event;

import lombok.Data;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventShortDto {
    // Краткая информация о событии

    private final Long id; // Идентификатор
    private final String annotation; // Краткое описание
    private final CategoryDto category; // Категория
    private final Long confirmedRequests; // Количество одобренных заявок на участие в данном событии
    private final LocalDateTime eventDate; // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    private final UserShortDto initiator; // Пользователь (краткая информация)
    private final Boolean paid; // Нужно ли оплачивать участие
    private final String title; // Заголовок
    private final Long views; // Количество просмотров события

}

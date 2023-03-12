package ru.practicum.explorewithme.dto.event;

import lombok.Data;
import ru.practicum.explorewithme.dto.Location;

@Data
public class UpdateEventUserRequest {
    // Данные для изменения информации о событии. Если поле в запросе не указано (равно null) -
    // значит изменение этих данных не треубется.

    private final String annotation; // Новая аннотация: maxLength: 2000; minLength: 20;
    private final Long category; // Новая категория
    private final String description; // Новое описание: maxLength: 7000; minLength: 20;
    private final String eventDate; // Новые дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"
    private final Location location;
    private final Boolean paid; // Новое значение флага о платности мероприятия
    private final Long participantLimit; // Новый лимит пользователей
    private final Boolean requestModeration; // Нужна ли пре-модерация заявок на участие
    private final StateAction stateAction; // Изменение сотояния события: SEND_TO_REVIEW, CANCEL_REVIEW
    private final String title; // Новый заголовок: maxLength: 120; minLength: 3;

}

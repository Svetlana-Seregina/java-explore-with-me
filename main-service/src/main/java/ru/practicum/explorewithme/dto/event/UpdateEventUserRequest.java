package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explorewithme.dto.Location;
import ru.practicum.explorewithme.dto.category.CategoryDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000)
    private final String annotation; // Новая аннотация

    private final CategoryDto category; // Новая категория

    @Size(min = 20, max = 7000)
    private final String description; // Новое описание

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate; // Новые дата и время на которые намечено событие

    private final Location location;

    private final Boolean paid; // Новое значение флага о платности мероприятия

    private final Long participantLimit; // Новый лимит пользователей

    private final Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

    private final String stateAction; // Изменение сотояния события: SEND_TO_REVIEW, CANCEL_REVIEW

    @Size(min = 3, max = 120)
    private final String title; // Новый заголовок

}

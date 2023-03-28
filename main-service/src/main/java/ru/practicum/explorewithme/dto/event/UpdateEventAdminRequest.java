package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.practicum.explorewithme.dto.Location;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000)
    private String annotation; // Новая аннотация

    private Long category; // Новая категория

    @Size(min = 20, max = 7000)
    private String description; // Новое описание

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate; // Новые дата и время на которые намечено событие. Дата и время указываются в формате "yyyy-MM-dd HH:mm:ss"

    private Location location;

    private Boolean paid; // Новое значение флага о платности мероприятия

    private Long participantLimit; // Новый лимит пользователей

    private Boolean requestModeration; // Нужна ли пре-модерация заявок на участие

    private String stateAction; // Новое состояние события

    @Size(min = 3, max = 120)
    private String title; // Новый заголовок: maxLength: 120; minLength: 3;

}

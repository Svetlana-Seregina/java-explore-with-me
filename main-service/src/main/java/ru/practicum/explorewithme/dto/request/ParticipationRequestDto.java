package ru.practicum.explorewithme.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParticipationRequestDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime created; // Дата и время создания заявки, example "2022-09-06T21:10:05.432"

    private final Long event; // Идентификатор события

    private final Long id; // Идентификатор заявки

    private final Long requester; // Идентификатор пользователя, отправившего заявку

    private final String status; // Статус заявки EventRequestStatus

}

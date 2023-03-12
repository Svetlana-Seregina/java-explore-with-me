package ru.practicum.explorewithme.dto.event;

import lombok.Data;

@Data
public class EventRequestStatusUpdateRequest {
    // Изменение статуса запроса на участие в событии текущего пользователя
    private final Long requestIds;
    private final EventRequestStatus status; // Новый статус запроса на участие в событии текущего пользователя
    // CONFIRMED, REJECTED
}

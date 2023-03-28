package ru.practicum.explorewithme.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {
    // Изменение статуса запроса на участие в событии текущего пользователя
    private final List<Long> requestIds; // Идентификаторы запросов на участие в событии текущего пользователя

    private final String status; // Новый статус запроса на участие в событии текущего пользователя // CONFIRMED, REJECTED

}

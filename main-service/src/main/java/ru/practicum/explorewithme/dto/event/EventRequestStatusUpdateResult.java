package ru.practicum.explorewithme.dto.event;

import lombok.Data;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;

@Data
public class EventRequestStatusUpdateResult {
    // Результат подтверждения/отклонения заявок на участие в событии

    private final ParticipationRequestDto confirmedRequests; // Заявка на участие в событии
    private final ParticipationRequestDto rejectedRequests; // Заявка на участие в событии
}

package ru.practicum.explorewithme.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class EventRequestStatusUpdateResult {
    // Результат подтверждения/отклонения заявок на участие в событии

    private final List<ParticipationRequestDto> confirmedRequests; // Заявки на участие в событии

    private final List<ParticipationRequestDto> rejectedRequests; // Заявки на участие в событии

}

package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.event.*;

import java.util.List;

public interface PrivateService {
    EventShortDto findEventByUser(long id, Integer from, Integer size);

    EventFullDto createNewEvent(long userId, NewEventDto newEventDto);

    EventFullDto findEventByUser(long userId, long eventId);

    EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> findAllEventRequestsByUser(long userId, long eventId);

    List<EventRequestStatusUpdateResult> updateRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getRequestsByUser(long userId);

    ParticipationRequestDto createRequestByUser(long userId);

    ParticipationRequestDto cancelEventRequestByUser(long userId, long requestId);
}

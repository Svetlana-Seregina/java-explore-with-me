package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventDto;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;

import java.util.List;

public interface PrivateService {

    List<EventShortDto> findAllEventsByInitiator(long id, Integer from, Integer size);

    EventFullDto createNewEvent(long userId, NewEventDto newEventDto);

    EventFullDto findEventByInitiator(long userId, long eventId);

    EventFullDto updateEventByInitiator(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> findAllEventRequestsByInitiator(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestStatusByInitiator(long userId, long eventId,
                                                                  EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getOwnRequestsByRequester(long userId);

    ParticipationRequestDto createRequestByRequester(long userId, long eventId);

    ParticipationRequestDto cancelEventRequestByRequester(long userId, long requestId);

}

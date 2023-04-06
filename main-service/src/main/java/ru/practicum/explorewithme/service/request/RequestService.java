package ru.practicum.explorewithme.service.request;

import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    List<ParticipationRequestDto> findAllEventRequestsByInitiator(long userId, long eventId);

    EventRequestStatusUpdateResult updateRequestStatusByInitiator(long userId, long eventId,
                                                                  EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getOwnRequestsByRequester(long userId);

    ParticipationRequestDto createRequestByRequester(long userId, long eventId);

    ParticipationRequestDto cancelEventRequestByRequester(long userId, long requestId);

}

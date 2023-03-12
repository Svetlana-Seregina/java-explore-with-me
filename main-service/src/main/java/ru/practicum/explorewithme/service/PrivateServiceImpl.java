package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.event.*;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PrivateServiceImpl implements PrivateService {


    @Override
    public EventShortDto findEventByUser(long id, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventFullDto createNewEvent(long userId, NewEventDto newEventDto) {
        return null;
    }

    @Override
    public EventFullDto findEventByUser(long userId, long eventId) {
        return null;
    }

    @Override
    public EventFullDto updateEventByUser(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        return null;
    }

    @Override
    public List<ParticipationRequestDto> findAllEventRequestsByUser(long userId, long eventId) {
        return null;
    }

    @Override
    public List<EventRequestStatusUpdateResult> updateRequestStatus(long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return null;
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUser(long userId) {
        return null;
    }

    @Override
    public ParticipationRequestDto createRequestByUser(long userId) {
        return null;
    }

    @Override
    public ParticipationRequestDto cancelEventRequestByUser(long userId, long requestId) {
        return null;
    }
}

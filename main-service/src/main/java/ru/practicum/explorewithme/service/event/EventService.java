package ru.practicum.explorewithme.service.event;

import ru.practicum.explorewithme.dto.event.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    List<EventFullDto> findAllEvents(List<Long> users, List<String> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventById(UpdateEventAdminRequest updateEventAdminRequest, long eventId);

    List<EventShortDto> findAllEventsByInitiator(long id, Integer from, Integer size);

    EventFullDto createNewEvent(long userId, NewEventDto newEventDto);

    EventFullDto findEventByInitiator(long userId, long eventId);

    EventFullDto updateEventByInitiator(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                      String path);

    EventFullDto findEventById(long id, String path);

}

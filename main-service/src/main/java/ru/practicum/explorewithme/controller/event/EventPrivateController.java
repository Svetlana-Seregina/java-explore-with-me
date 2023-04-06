package ru.practicum.explorewithme.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventDto;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.service.event.EventService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class EventPrivateController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findAllEventsByInitiator(@PathVariable long userId,
                                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабатываем запрос на поиск всех событий инициатором с id = {}", userId);
        return eventService.findAllEventsByInitiator(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> createNewEvent(@PathVariable long userId,
                                                       @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Обрабатываем запрос на создание события инициатором с id = {}, newEventDto = {}", userId, newEventDto);
        EventFullDto eventFullDto = eventService.createNewEvent(userId, newEventDto);
        return new ResponseEntity<>(eventFullDto, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findEventByInitiator(@PathVariable long userId,
                                             @PathVariable long eventId) {
        log.info("Обрабатываем запрос на поиск события инициатором userId = {}, eventId = {}", userId, eventId);
        return eventService.findEventByInitiator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByInitiator(@PathVariable long userId,
                                               @PathVariable long eventId,
                                               @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Обрабатываем запрос на обновление события. Данные для обновления события: annotation = {}; category = {}; description = {}, " +
                        "eventDate = {}, paid = {}, participantLimit = {}, " +
                        "requestModeration = {}, stateAction = {}, title = {}, ",
                updateEventUserRequest.getAnnotation(), updateEventUserRequest.getCategory(),
                updateEventUserRequest.getDescription(), updateEventUserRequest.getEventDate(),
                updateEventUserRequest.getPaid(), updateEventUserRequest.getParticipantLimit(),
                updateEventUserRequest.getRequestModeration(), updateEventUserRequest.getStateAction(), updateEventUserRequest.getTitle());

        return eventService.updateEventByInitiator(userId, eventId, updateEventUserRequest);
    }

}

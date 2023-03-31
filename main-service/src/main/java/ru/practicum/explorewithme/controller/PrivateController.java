package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.NewEventDto;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.service.PrivateService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateController {

    private final PrivateService privateService;

    @GetMapping("/{userId}/events")
    public List<EventShortDto> findAllEventsByInitiator(@PathVariable long userId,
                                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабатываем запрос на поиск всех событий инициатором с id = {}", userId);
        return privateService.findAllEventsByInitiator(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public ResponseEntity<EventFullDto> createNewEvent(@PathVariable long userId,
                                                       @RequestBody @Valid NewEventDto newEventDto) {
        log.info("Обрабатываем запрос на создание события инициатором с id = {}, newEventDto = {}", userId, newEventDto);
        EventFullDto eventFullDto = privateService.createNewEvent(userId, newEventDto);
        return new ResponseEntity<>(eventFullDto, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findEventByInitiator(@PathVariable long userId,
                                             @PathVariable long eventId) {
        log.info("Обрабатываем запрос на поиск события инициатором userId = {}, eventId = {}", userId, eventId);
        return privateService.findEventByInitiator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByInitiator(@PathVariable long userId,
                                               @PathVariable long eventId,
                                               @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("Обрабатываем запрос на обновление события. Данные для обновления события: annotation = {}; category = {}; description = {}, " +
                        "eventDate = {}, location = {}, paid = {}, participantLimit = {}, " +
                        "requestModeration = {}, stateAction = {}, title = {}, ",
                updateEventUserRequest.getAnnotation(), updateEventUserRequest.getCategory(),
                updateEventUserRequest.getDescription(), updateEventUserRequest.getEventDate(),
                updateEventUserRequest.getLocation(), updateEventUserRequest.getPaid(),
                updateEventUserRequest.getParticipantLimit(), updateEventUserRequest.getRequestModeration(),
                updateEventUserRequest.getStateAction(), updateEventUserRequest.getTitle());

        return privateService.updateEventByInitiator(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findAllEventRequestsByInitiator(@PathVariable long userId,
                                                                         @PathVariable long eventId) {
        log.info("Обрабатываем запрос на поиск всех запросов для участия в событии инициатором userId = {}, eventId = {}", userId, eventId);
        return privateService.findAllEventRequestsByInitiator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatusByInitiator(@PathVariable long userId,
                                                                         @PathVariable long eventId,
                                                                         @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Обрабатываем запрос на обновление статуса запроса для участия в событии инициатора " +
                "userId = {}, eventId = {}, eventRequestStatusUpdateRequest = {}", userId, eventId, eventRequestStatusUpdateRequest);
        return privateService.updateRequestStatusByInitiator(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getOwnRequestsByRequester(@PathVariable long userId) {
        log.info("Обрабатываем запрос на получение заявок от пользователя-участника с userId = {}", userId);
        return privateService.getOwnRequestsByRequester(userId);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> createRequestByRequester(@PathVariable long userId,
                                                                            @RequestParam("eventId") long eventId) {
        log.info("Обрабатываем запрос на создание заявки от пользователя с userId = {} на участие в событии с eventId = {}", userId, eventId);
        ParticipationRequestDto participationRequestDto = privateService.createRequestByRequester(userId, eventId);
        return new ResponseEntity<>(participationRequestDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelEventRequestByRequester(@PathVariable long userId,
                                                                 @PathVariable long requestId) {
        log.info("Обрабатываем запрос на отмену своего участия в событии от userId = {} событие requestId = {}", userId, requestId);
        return privateService.cancelEventRequestByRequester(userId, requestId);
    }

}


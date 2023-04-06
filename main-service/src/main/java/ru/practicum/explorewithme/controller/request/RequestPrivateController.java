package ru.practicum.explorewithme.controller.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.service.request.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class RequestPrivateController {

    private final RequestService requestService;

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findAllEventRequestsByInitiator(@PathVariable long userId,
                                                                         @PathVariable long eventId) {
        log.info("Обрабатываем запрос на поиск всех запросов для участия в событии инициатором userId = {}, eventId = {}", userId, eventId);
        return requestService.findAllEventRequestsByInitiator(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatusByInitiator(@PathVariable long userId,
                                                                         @PathVariable long eventId,
                                                                         @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Обрабатываем запрос на обновление статуса запроса для участия в событии инициатора " +
                "userId = {}, eventId = {}, eventRequestStatusUpdateRequest = {}", userId, eventId, eventRequestStatusUpdateRequest);
        return requestService.updateRequestStatusByInitiator(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getOwnRequestsByRequester(@PathVariable long userId) {
        log.info("Обрабатываем запрос на получение заявок от пользователя-участника с userId = {}", userId);
        return requestService.getOwnRequestsByRequester(userId);
    }

    @PostMapping("/{userId}/requests")
    public ResponseEntity<ParticipationRequestDto> createRequestByRequester(@PathVariable long userId,
                                                                            @RequestParam("eventId") long eventId) {
        log.info("Обрабатываем запрос на создание заявки от пользователя с userId = {} на участие в событии с eventId = {}", userId, eventId);
        ParticipationRequestDto participationRequestDto = requestService.createRequestByRequester(userId, eventId);
        return new ResponseEntity<>(participationRequestDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelEventRequestByRequester(@PathVariable long userId,
                                                                 @PathVariable long requestId) {
        log.info("Обрабатываем запрос на отмену своего участия в событии от userId = {} событие requestId = {}", userId, requestId);
        return requestService.cancelEventRequestByRequester(userId, requestId);
    }

}


package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.service.PrivateService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateController {

    private final PrivateService privateService;

    @GetMapping("/{userId}/events")
    public EventShortDto findEventByUser(@PathVariable long userId,
                                         @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return privateService.findEventByUser(userId, from, size);
    }

    @PostMapping("/{userId}/events")
    public EventFullDto createNewEvent(@PathVariable long userId,
                                       @RequestBody NewEventDto newEventDto) {
        return privateService.createNewEvent(userId, newEventDto);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto findEventByUser(@PathVariable long userId,
                                        @PathVariable long eventId) {
        return privateService.findEventByUser(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEventByUser(@PathVariable long userId,
                                          @PathVariable long eventId,
                                          @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        return privateService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findAllEventRequestsByUser(@PathVariable long userId,
                                                                    @PathVariable long eventId) {
        return privateService.findAllEventRequestsByUser(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public List<EventRequestStatusUpdateResult> updateRequestStatus(@PathVariable long userId,
                                                                    @PathVariable long eventId,
                                                                    @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return privateService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsByUser(@PathVariable long userId) {
        return privateService.getRequestsByUser(userId);
    }

    @PostMapping("/{userId}/requests")
    public ParticipationRequestDto createRequestByUser(@PathVariable long userId) {
        return privateService.createRequestByUser(userId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelEventRequestByUser(@PathVariable long userId,
                                                            @PathVariable long requestId) {
        return privateService.cancelEventRequestByUser(userId, requestId);
    }
}


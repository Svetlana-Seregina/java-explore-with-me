package ru.practicum.explorewithme.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.UpdateEventAdminRequest;
import ru.practicum.explorewithme.service.event.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class EventAdminController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findAllEvents(@RequestParam(required = false) List<Long> users,
                                            @RequestParam(required = false) List<String> states,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабытываем запрос на поиск всех событий по заданным требованиям: users = {}; states = {}; " +
                        "categories = {}; rangeStart = {}; rangeEnd = {}; from = {}, size = {}", users, states, categories,
                rangeStart, rangeEnd, from, size);
        return eventService.findAllEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventById(@PathVariable long eventId,
                                        @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Обрабатываем запрос на редактирование события. Данные для редактирования = {}", updateEventAdminRequest);
        return eventService.updateEventById(updateEventAdminRequest, eventId);
    }

}

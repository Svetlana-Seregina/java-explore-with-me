package ru.practicum.explorewithme.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.service.EndpointHitService;
import ru.practicum.explorewithme.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/events")
@RestController
@RequiredArgsConstructor
@Slf4j
public class EventPublicController {

    private final EventService eventService;
    private final EndpointHitService endpointHitService;

    @GetMapping
    public List<EventShortDto> findAllEvents(@RequestParam(value = "text", required = false) String text,
                                             @RequestParam(value = "categories", required = false) List<Long> categories,
                                             @RequestParam(value = "paid", required = false) Boolean paid,
                                             @RequestParam(value = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                             @RequestParam(value = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                             @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                             @RequestParam(value = "sort", defaultValue = "EVENT_DATE") String sort, // EVENT_DATE, VIEWS
                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                                             HttpServletRequest request) {
        log.info("Параметры запроса для поиска событий: text = {}; categories = {}; " +
                        "paid = {}; rangeStart = {}; rangeEnd = {}; onlyAvailable = {}; sort = {}; from = {}; size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        String path = request.getRequestURI();

        endpointHitService.createEndpointHit(request);

        List<EventShortDto> eventShortDtos = eventService.findAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, path);

        log.info("Получен список событий для пользователя по параметрам: {}", eventShortDtos);

        return eventShortDtos;
    }

    @GetMapping("/{id}")
    public EventFullDto findEventById(@PathVariable long id, HttpServletRequest request) {
        log.info("Обрабатываем запрос на поиск события по id = {}", id);
        String path = request.getRequestURI();
        log.info("endpoint path: {}", path);

        EventFullDto eventFullDto = eventService.findEventById(id, path);

        endpointHitService.createEndpointHit(request);

        return eventFullDto;
    }

}

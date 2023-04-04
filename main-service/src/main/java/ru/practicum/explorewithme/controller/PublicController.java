package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.EndpointHitDto;
import ru.practicum.explorewithme.StatsClient;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.service.PublicService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicController {

    private final PublicService publicService;
    private final StatsClient statsClient;

    @GetMapping("/compilations")
    public List<CompilationDto> findAllCompilations(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабатываем запрос на поиск всех подборок закреплены(pinned)? = {}", pinned);
        return publicService.findAllCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto findCompilationById(@PathVariable long compId) {
        log.info("Обрабатываем запрос на поиск подборки по id = {}", compId);
        return publicService.findCompilationById(compId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> findAllCategories(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабатываем запрос на поиск всех категорий.");
        return publicService.findAllCategories(from, size);
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> findCategoryById(@PathVariable long catId) {
        log.info("Обрабатываем запрос на поиск категории по id = {}", catId);
        try {
            CategoryDto category = publicService.findCategoryById(catId);
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/events")
    public List<EventShortDto> findAllEvents(@RequestParam(value = "text", required = false) String text,
                                             @RequestParam(value = "categories", required = false) List<Long> categories,
                                             @RequestParam(value = "paid", required = false) Boolean paid,
                                             @RequestParam(value = "rangeStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                             @RequestParam(value = "rangeEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                             @RequestParam(value = "onlyAvailable", required = false) Boolean onlyAvailable,
                                             @RequestParam(value = "sort", required = false) String sort, // EVENT_DATE, VIEWS
                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size,
                                             HttpServletRequest request) {
        log.info("Параметры запроса для поиска событий: text = {}; categories = {}; " +
                        "paid = {}; rangeStart = {}; rangeEnd = {}; onlyAvailable = {}; sort = {}; from = {}; size = {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        String path = request.getRequestURI();
        List<EventShortDto> eventShortDtos = publicService.findAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, path);

        List<String> uris = new ArrayList<>();
        for (EventShortDto event : eventShortDtos) {
            String uri = path + "/" + event.getId();
            uris.add(uri);
        }

        String app = "ewm-main-service";
        String ip = request.getRemoteAddr();
        log.info("client ip (findAllEvents): {}", ip);
        log.info("client uri (findAllEvents): {}", uris);

        for (String uri : uris) {
            EndpointHitDto endpointHitDto = new EndpointHitDto(app, uri, ip, LocalDateTime.now());
            log.info("Передаем endpointHitDto в statsClient: {}", endpointHitDto);
            statsClient.save(endpointHitDto);
        }
        log.info("Создан список для пользователя по параметрам: {}", eventShortDtos);

        return eventShortDtos;
    }

    @GetMapping("/events/{id}")
    public EventFullDto findEventById(@PathVariable long id, HttpServletRequest request) {
        log.info("Обрабатываем запрос на поиск события по id = {}", id);
        String ip = request.getRemoteAddr();
        log.info("client ip: {}", ip);
        String path = request.getRequestURI();
        log.info("endpoint path: {}", path);
        String app = "ewm-main-service";

        EventFullDto eventFullDto = publicService.findEventById(id, path);

        EndpointHitDto endpointHitDto = new EndpointHitDto(app, path, ip, LocalDateTime.now());
        log.info("Передаем endpointHitDto в statsClient: {}", endpointHitDto);
        statsClient.save(endpointHitDto);
        return eventFullDto;
    }

}

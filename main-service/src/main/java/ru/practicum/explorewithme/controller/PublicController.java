package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.service.PublicService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PublicController {

    private final PublicService publicService;

    @GetMapping("/compilations")
    public List<CompilationDto> findAllCompilations(@RequestParam(value = "pinned") Boolean pinned,
                                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return publicService.findAllCompilations(pinned, from, size);
    }

    @GetMapping("/compilations/{id}")
    public List<CompilationDto> findCompilationsById(@PathVariable long id) {
        return publicService.findCompilationsById(id);
    }

    @GetMapping("/categories")
    public List<CategoryDto> findAllCategories(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return publicService.findAllCategories(from, size);
    }

    @GetMapping("/categories/{id}")
    public CategoryDto findCategoriesById(@PathVariable long id) {
        return publicService.findCategoriesById(id);
    }

    @GetMapping("/events")
    public List<EventShortDto> findAllEvents(@RequestParam(value = "text") String text,
                                             @RequestParam(value = "categories") List<Integer> categories,
                                             @RequestParam(value = "paid") Boolean paid,
                                             @RequestParam(value = "rangeStart") LocalDateTime rangeStart,
                                             @RequestParam(value = "rangeEnd") LocalDateTime rangeEnd,
                                             @RequestParam(value = "onlyAvailable") Boolean onlyAvailable,
                                             @RequestParam(value = "sort") String sort, // EVENT_DATE, VIEWS
                                             @RequestParam(value = "from", defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return publicService.findAllEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

    @GetMapping("/events/{id}")
    public EventFullDto findEventById(@PathVariable long id) {
        return publicService.findEventById(id);
    }

}

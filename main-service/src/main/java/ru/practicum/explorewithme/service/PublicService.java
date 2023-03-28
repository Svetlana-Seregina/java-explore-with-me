package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface PublicService {

    List<CompilationDto> findAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto findCompilationById(long compId);

    List<CategoryDto> findAllCategories(Integer from, Integer size);

    CategoryDto findCategoryById(long catId);

    List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size,
                                      String path);

    EventFullDto findEventById(long id, String path);

}

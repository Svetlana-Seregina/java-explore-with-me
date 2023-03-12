package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PublicServiceImpl implements PublicService {

    @Override
    public List<CompilationDto> findAllCompilations(Boolean pinned, Integer from, Integer size) {
        return null;
    }

    @Override
    public List<CompilationDto> findCompilationsById(long id) {
        return null;
    }

    @Override
    public List<CategoryDto> findAllCategories(Integer from, Integer size) {
        return null;
    }

    @Override
    public CategoryDto findCategoriesById(long id) {
        return null;
    }

    @Override
    public List<EventShortDto> findAllEvents(String text, List<Integer> categories, Boolean paid,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                             String sort, Integer from, Integer size) {
        return null;
    }

    @Override
    public EventFullDto findEventById(long id) {
        return null;
    }

}

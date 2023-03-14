package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.repository.CategoryRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PublicServiceImpl implements PublicService {

    private final CategoryRepository categoryRepository;

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
        List<CategoryDto> categoryDtoList = categoryRepository.findAll();
        if (categoryDtoList.size() == 0) {
            return Collections.emptyList();
        }
        log.info("Найдено {} категорий", categoryDtoList.size());
        return categoryDtoList;
    }

    @Override
    public CategoryDto findCategoryById(long catId) {
        CategoryDto category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена в базе"));
        log.info("Найдена категория с названием = {}", category.getName());
        return category;
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

package ru.practicum.explorewithme.service.category;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.Category;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.mappers.CategoryMapper;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        log.info("Содана новая категория = {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @SneakyThrows
    @Transactional
    @Override
    public boolean deleteCategoryById(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категории с id = %d нет в базе.", catId)));
        List<Event> events = eventRepository.findAllByCategory(category);
        if (!events.isEmpty()) {
            throw new ValidationException(String.format("Категория не может быть удалена, т.к. с ней связаны события." +
                    " Количество событий = %d", events.size()));
        }
        categoryRepository.deleteById(catId);
        log.info("Категория с id = {} удалена.", catId);
        return categoryRepository.existsById(catId);
    }

    @Transactional
    @Override
    public CategoryDto updateCategoryName(long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категории с id = %d нет в базе.", id)));
        log.info("Найдена категория = {}", category);

        if (categoryDto.getName() != null && !categoryDto.getName().isBlank()) {
            String name = categoryDto.getName();
            if (categoryRepository.findByName(name).isPresent()) {
                throw new ValidationException(String.format("Категория с таким именем = %s уже существует в базе.", name));
            }
            category.setName(name);
            log.info("Имя категории изменено на {}, id = {}", category.getName(), category.getId());
        }
        return CategoryMapper.toCategoryDto(category);
    }

    @Override
    public List<CategoryDto> findAllCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Category> categories = categoryRepository.findAll(pageable)
                .stream().collect(Collectors.toList());
        if (categories.size() == 0) {
            return Collections.emptyList();
        }
        log.info("Найдено {} категорий", categories.size());
        return categories
                .stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(toList());
    }

    @Override
    public CategoryDto findCategoryById(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена в базе"));
        log.info("Найдена категория = {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

}

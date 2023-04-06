package ru.practicum.explorewithme.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.service.category.CategoryService;

import java.util.List;

@RequestMapping("/categories")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> findAllCategories(@RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабатываем запрос на поиск всех категорий.");
        return categoryService.findAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    public ResponseEntity<CategoryDto> findCategoryById(@PathVariable long catId) {
        log.info("Обрабатываем запрос на поиск категории по id = {}", catId);
        try {
            CategoryDto category = categoryService.findCategoryById(catId);
            return new ResponseEntity<>(category, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}

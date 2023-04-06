package ru.practicum.explorewithme.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.service.category.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Обрабатываем запрос на создание категории. Данные для создания категории newCategoryDto = {}", newCategoryDto);
        CategoryDto categoryDto = categoryService.createCategory(newCategoryDto);
        return new ResponseEntity<>(categoryDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<Boolean> deleteCategoryById(@PathVariable long catId) {
        log.info("Обрабатываем запрос на удаление категории. Создан запрос на удаление категории по id = {}", catId);
        boolean categoryDelete = categoryService.deleteCategoryById(catId);
        return new ResponseEntity<>(categoryDelete, HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategoryName(@PathVariable long catId,
                                                          @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Обрабатываем запрос на обновление категории. Редактирование категории по id = {}, categoryDto = {}", catId, categoryDto);
        CategoryDto category = categoryService.updateCategoryName(catId, categoryDto);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }

}

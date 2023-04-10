package ru.practicum.explorewithme.service.category;

import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    boolean deleteCategoryById(long catId);

    CategoryDto updateCategoryName(long id, CategoryDto categoryDto);

    List<CategoryDto> findAllCategories(Integer from, Integer size);

    CategoryDto findCategoryById(long catId);

}

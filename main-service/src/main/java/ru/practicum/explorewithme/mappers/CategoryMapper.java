package ru.practicum.explorewithme.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.category.Category;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;

@UtilityClass
public class CategoryMapper {

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }
}

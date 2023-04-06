package ru.practicum.explorewithme.dto.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {

    private final Long id;
    @NotBlank
    private final String name;

}

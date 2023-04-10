package ru.practicum.explorewithme.dto.category;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CategoryDto {

    private final Long id;

    @NotBlank
    @Size(max = 512)
    private final String name;

}

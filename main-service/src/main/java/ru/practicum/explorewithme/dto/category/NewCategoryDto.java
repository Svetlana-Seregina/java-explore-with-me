package ru.practicum.explorewithme.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {

    @NotBlank
    @Column(unique = true)
    private String name;

}

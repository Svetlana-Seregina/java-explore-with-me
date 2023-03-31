package ru.practicum.explorewithme.dto.compilation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class NewCompilationDto {

    private final List<Long> events;

    private final Boolean pinned;

    @NotBlank
    private final String title;

}

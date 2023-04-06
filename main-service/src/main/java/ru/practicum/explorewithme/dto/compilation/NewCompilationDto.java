package ru.practicum.explorewithme.dto.compilation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class NewCompilationDto {

    private final List<Long> events;

    private final boolean pinned;

    @NotBlank
    @Size(max = 512)
    private final String title;

}

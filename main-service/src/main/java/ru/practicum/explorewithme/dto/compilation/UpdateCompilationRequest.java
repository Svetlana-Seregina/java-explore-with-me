package ru.practicum.explorewithme.dto.compilation;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class UpdateCompilationRequest {

    private final List<Long> events;

    private final Boolean pinned;

    @Size(max = 512)
    private final String title;

}

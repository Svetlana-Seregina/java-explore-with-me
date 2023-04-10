package ru.practicum.explorewithme.dto.compilation;

import lombok.Data;
import ru.practicum.explorewithme.dto.event.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {

    private final List<EventShortDto> events;

    private final Long id;

    private final Boolean pinned;

    private final String title;

}

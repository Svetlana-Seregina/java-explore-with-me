package ru.practicum.explorewithme.dto.compilation;

import lombok.Data;
import ru.practicum.explorewithme.dto.event.EventShortDto;

import java.util.List;

@Data
public class CompilationDto {

    private final List<EventShortDto> events;

    private final Long id; // Идентификатор

    private final Boolean pinned; // Закреплена ли подборка на главной странице сайта

    private final String title; // Заголовок подборки

}

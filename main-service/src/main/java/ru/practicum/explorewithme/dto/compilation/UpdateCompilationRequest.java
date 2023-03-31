package ru.practicum.explorewithme.dto.compilation;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCompilationRequest {

    private final List<Long> events;

    private final Boolean pinned;

    private final String title;

}

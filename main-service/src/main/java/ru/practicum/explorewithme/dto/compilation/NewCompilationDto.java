package ru.practicum.explorewithme.dto.compilation;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class NewCompilationDto {

    private final List<Long> events; // Список идентификаторов событий входящих в подборку

    private final Boolean pinned; // Закреплена ли подборка на главной странице сайта

    @NotBlank
    private final String title; // Заголовок подборки

}

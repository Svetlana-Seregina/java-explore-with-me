package ru.practicum.explorewithme.dto.compilation;

import lombok.Data;

@Data
public class NewCompilationDto {
    // Подборка событий
    private final Long events; // Список идентификаторов событий входящих в подборку
    private final Boolean pinned; // Закреплена ли подборка на главной странице сайта
    private final String title; // Заголовок подборки

}

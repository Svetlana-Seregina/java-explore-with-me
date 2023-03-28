package ru.practicum.explorewithme.dto.compilation;

import lombok.Data;

import java.util.List;

@Data
public class UpdateCompilationRequest {

    private final List<Long> events; // Список id событий подборки для полной замены текущего списка

    private final Boolean pinned; // Закреплена ли подборка на главной странице сайта

    private final String title; // Заголовок подборки

}

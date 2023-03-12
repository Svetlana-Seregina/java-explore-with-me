package ru.practicum.explorewithme.dto.compilation;

import lombok.Data;

@Data
public class UpdateCompilationRequest {
    // Изменение информации о подборке событий. Если поле в запросе не указано (равно null) -
    // значит изменение этих данных не треубется.
    private final Long events; // Список id событий подборки для полной замены текущего списка
    private final Boolean pinned; // Закреплена ли подборка на главной странице сайта
    private final String title; // Заголовок подборки
}

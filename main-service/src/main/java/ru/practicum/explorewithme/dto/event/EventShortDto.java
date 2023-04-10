package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventShortDto {
    // Краткая информация о событии

    private final String annotation;

    private final CategoryDto category;

    private final Long confirmedRequests;

    private final List<CommentDto> comments;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;

    private final Long id;

    private final UserShortDto initiator;

    private final Boolean paid;

    private final String title;

    private final Long views;

}

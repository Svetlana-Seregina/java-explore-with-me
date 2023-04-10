package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;

import java.time.LocalDateTime;

@Data
public class EventFullDto {

    private final String annotation;

    private final CategoryDto category;

    private final Long confirmedRequests;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime createdOn;

    private final String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;

    private final Long id;

    private final UserShortDto initiator;

    private final Location location;

    private final Boolean paid;

    private final Long participantLimit;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime publishedOn;

    private final Boolean requestModeration;

    private final EventState state;

    private final String title;

    private final Long views;

    @Data
    public static class Location {
        private final double lat;
        private final double lon;
    }

}

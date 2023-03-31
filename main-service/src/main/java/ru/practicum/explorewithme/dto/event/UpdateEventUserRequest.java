package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.explorewithme.dto.Location;
import ru.practicum.explorewithme.dto.category.CategoryDto;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000)
    private final String annotation;

    private final CategoryDto category;

    @Size(min = 20, max = 7000)
    private final String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;

    private final Location location;

    private final Boolean paid;

    private final Long participantLimit;

    private final Boolean requestModeration;

    private final String stateAction; // Изменение сотояния события: SEND_TO_REVIEW, CANCEL_REVIEW

    @Size(min = 3, max = 120)
    private final String title;

}

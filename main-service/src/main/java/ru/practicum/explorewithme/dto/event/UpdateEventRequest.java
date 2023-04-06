package ru.practicum.explorewithme.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class UpdateEventRequest {

    @Size(min = 20, max = 2000)
    protected String annotation;

    protected Long category;

    @Size(min = 20, max = 7000)
    protected String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime eventDate;

    protected Boolean paid;

    @PositiveOrZero
    protected Long participantLimit;

    protected Boolean requestModeration;

    protected String stateAction;

    @Size(min = 3, max = 120)
    protected String title;

}

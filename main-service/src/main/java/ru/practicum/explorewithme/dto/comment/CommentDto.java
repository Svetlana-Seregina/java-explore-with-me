package ru.practicum.explorewithme.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private final String text;

    private final String userName;

    private final Long eventId;

    private final LocalDateTime createdDate;

}
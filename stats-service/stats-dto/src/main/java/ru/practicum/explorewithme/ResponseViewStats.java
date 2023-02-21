package ru.practicum.explorewithme;

import lombok.Data;

@Data
public class ResponseViewStats {
    private final String app;
    private final String uri;
    private final Integer hits;
}

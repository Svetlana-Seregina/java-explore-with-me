package ru.practicum.explorewithme;

import lombok.Data;

import java.util.List;

@Data
public class ResponseEndpointHit {
    private final String app;
    private final String uri;
    List<String> hits;
}

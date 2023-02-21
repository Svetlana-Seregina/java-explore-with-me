package ru.practicum.explorewithme;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RequestEndpointHit {
    @NotBlank
    private final String app;
    @NotBlank
    private final String uri;
    @NotBlank
    private final String ip;
    private final String timestamp;
}

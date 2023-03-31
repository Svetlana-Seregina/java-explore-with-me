package ru.practicum.explorewithme;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void save(EndpointHitDto endpointHitDto);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}

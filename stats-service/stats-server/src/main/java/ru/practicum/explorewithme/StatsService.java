package ru.practicum.explorewithme;

import java.util.List;

public interface StatsService {

    void save(EndpointHitDto endpointHitDto);

    List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique);
}

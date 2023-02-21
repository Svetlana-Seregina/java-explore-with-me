package ru.practicum.explorewithme;

import java.util.List;

public interface StatsService {

    void save(RequestEndpointHit requestStatsDto);

    List<ResponseViewStats> findAll(String start, String end, List<String> uris, boolean unique);
}

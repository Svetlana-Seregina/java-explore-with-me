package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;

    @Transactional
    @Override
    public void save(EndpointHitDto requestStatsDto) {
        EndpointHit endpointHit = endpointHitRepository.save(MapperEndpointHit.toEndpointHit(requestStatsDto));
        log.info("Данные сохранены в БД, id = {}, объект = {}", endpointHit.getId(), endpointHit);
    }

    @Override
    public List<ViewStats> getStats(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startLdt = MapperEndpointHit.getLocalDateTime(start);
        LocalDateTime endLdt = MapperEndpointHit.getLocalDateTime(end);

        List<EndpointHit> allEndpoints = endpointHitRepository.findByTimestampAfterAndTimestampBefore(
                startLdt, endLdt);
        log.info("Получены данные из БД: {}", allEndpoints.size());
        log.info("Получены данные из БД, index 0: app = {}, uri = {}", allEndpoints.get(0).getApp(), allEndpoints.get(0).getUri());

        List<ViewStats> allViewStats = allEndpoints.stream()
                .map(MapperEndpointHit::toViewStats)
                .sorted(Comparator.comparing(ViewStats::getHits).reversed())
                .collect(Collectors.toList());

        log.info("allViewStats.size = {}", allViewStats.size());
        log.info("allViewStats.get(0) = {}", allViewStats.get(0));
        return allViewStats;
    }

}

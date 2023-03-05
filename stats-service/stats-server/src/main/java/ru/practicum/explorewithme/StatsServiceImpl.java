package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;
    private final ApplicationRepository applicationRepository;

    @Transactional
    @Override
    public void save(EndpointHitDto endpointHitDto) {
        String appName = endpointHitDto.getApp();
        Application app = applicationRepository.findByName(appName);
        if (app == null) {
            applicationRepository.save(MapperEndpointHit.toApplication(appName));
            app = applicationRepository.findByName(appName);
        }
        log.info("Получен app с id = {}, name = {}", app.getId(), app.getName());
        EndpointHit endpointHit = endpointHitRepository.save(MapperEndpointHit.toEndpointHit(endpointHitDto, app));
        log.info("Данные сохранены в БД, id = {}, uri = {}, appName = {}, appId = {}",
                endpointHit.getId(), endpointHit.getUri(), endpointHit.getApp().getName(), endpointHit.getApp().getId());
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            if (uris.isEmpty()) {
                return endpointHitRepository.findViewStatsWhenUrisIsEmptyAndIpIsUnique(start, end);
            } else {
                return endpointHitRepository.findViewStatsWhenUrisIsNotEmptyAndIpIsUnique(start, end, uris);
            }
        } else {
            if (uris.isEmpty()) {
                return endpointHitRepository.findViewStatsWhenUrisIsEmptyAndIpIsNotUnique(start, end);
            } else {
                return endpointHitRepository.findViewStatsWhenUrisIsNotEmptyAndIpIsNotUnique(start, end, uris);
            }
        }
    }

}

package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        Application app = applicationRepository.findByName(appName)
                .orElseGet(() -> applicationRepository.save(MapperEndpointHit.toApplication(appName)));
        log.info("Получен app с id = {}, name = {}", app.getId(), app.getName());
        EndpointHit endpointHit = endpointHitRepository.save(MapperEndpointHit.toEndpointHit(endpointHitDto, app));
        log.info("Данные сохранены в БД, id = {}, uri = {}, appName = {}, appId = {}",
                endpointHit.getId(), endpointHit.getUri(), endpointHit.getApp().getName(), endpointHit.getApp().getId());
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            if (uris.isEmpty()) {
                List<ViewStats> viewStats1 = endpointHitRepository.findViewStatsWhenUrisIsEmptyAndIpIsUnique(start, end);
                log.info("Найдены просмотры в БД, viewStats1 = {}", viewStats1);
                return viewStats1;
            }
            if (uris.get(0).equals("/events")) {
                List<ViewStats> viewStats2 = endpointHitRepository.findViewStatsWhenUrisIsEventsAndIpIsUnique(start, end);
                log.info("Найдены просмотры в БД, viewStats2 = {}", viewStats2);
                return viewStats2;
            } else {
                List<ViewStats> viewStats3 = endpointHitRepository.findViewStatsWhenUrisIsNotEmptyAndIpIsUnique(start, end, uris);
                log.info("Найдены просмотры в БД, viewStats3 = {}", viewStats3);
                return viewStats3;
            }
        } else {
            if (uris.isEmpty()) {
                List<ViewStats> viewStats3 = endpointHitRepository.findViewStatsWhenUrisIsEmptyAndIpIsNotUnique(start, end);
                log.info("Найдены просмотры в БД, viewStats3 = {}", viewStats3);
                return viewStats3;
            }
            if (uris.get(0).equals("/events")) {
                List<ViewStats> viewStats5 = endpointHitRepository.findViewStatsWhenUrisIsEventsAndIpIsNotUnique(start, end);
                log.info("Найдены просмотры в БД, viewStats5 = {}", viewStats5);
                return viewStats5;
            } else {
                List<ViewStats> viewStats4 = endpointHitRepository.findViewStatsWhenUrisIsNotEmptyAndIpIsNotUnique(start, end, uris);
                log.info("Найдены просмотры в БД, viewStats4 = {}", viewStats4);
                return viewStats4;
            }
        }
    }

}

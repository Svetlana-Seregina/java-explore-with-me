package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;

    @Transactional
    @Override
    public void save(RequestEndpointHit requestStatsDto) {
        EndpointHit endpointHit = endpointHitRepository.save(MapperEndpointHit.toEndpointHit(requestStatsDto));
        log.info("Данные сохранены в БД, id = {}, объект = {}", endpointHit.getId(), endpointHit);
    }

    @Override
    public List<ResponseViewStats> findAll(String start, String end, List<String> uris, boolean unique) {
        LocalDateTime startLdt = MapperEndpointHit.getLocalDateTime(start);
        LocalDateTime endLdt = MapperEndpointHit.getLocalDateTime(end);

        List<EndpointHit> allEndpoints = endpointHitRepository.findByTimestampAfterAndTimestampBeforeAndUriIn(
                startLdt, endLdt, uris);
        log.info("Получены данные из БД: {}", allEndpoints.size());
        Map<HitKey, Integer> keys = new HashMap<>();
        allEndpoints.forEach(hit -> {
            HitKey key = new HitKey(hit.getApp(), hit.getUri());
            Integer count = keys.getOrDefault(key, 0);
            keys.put(key, count + 1);
        });
        return keys.entrySet().stream()
                .map(entry -> new ResponseViewStats(entry.getKey().app, entry.getKey().uri, entry.getValue()))
                .collect(Collectors.toList());
    }

    static class HitKey {
        private final String app;
        private final String uri;

        public HitKey(String app, String uri) {
            this.app = app;
            this.uri = uri;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HitKey hitKey = (HitKey) o;
            return Objects.equals(app, hitKey.app) && Objects.equals(uri, hitKey.uri);
        }

        @Override
        public int hashCode() {
            return Objects.hash(app, uri);
        }
    }

}

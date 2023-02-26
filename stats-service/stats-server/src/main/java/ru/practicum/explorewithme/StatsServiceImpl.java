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
        Set<String> requiredUris = new HashSet<>(uris);

        List<EndpointHit> allEndpoints = endpointHitRepository.findByTimestampAfterAndTimestampBefore(
                startLdt, endLdt);
        log.info("Получены данные из БД: {}", allEndpoints.size());

        Map<HitKey, Map<String, Integer>> keys = new HashMap<>();
        allEndpoints.forEach(hit -> {
            if (!requiredUris.contains(hit.getUri())) {
                return;
            }
            HitKey key = new HitKey(hit.getApp(), hit.getUri());
            Map<String, Integer> mapByIp = keys.get(key);
            String ip = hit.getIp();
            if (mapByIp == null) {
                mapByIp = new HashMap<>();
                mapByIp.put(ip, 1);
                keys.put(key, mapByIp);
            } else {
                Integer count = mapByIp.getOrDefault(ip, 0);
                mapByIp.put(ip, count + 1);
            }
        });
        return keys.entrySet().stream()
                .map(entry -> new ViewStats(entry.getKey().app, entry.getKey().uri, getHitsCount(entry.getValue(), unique)))
                .collect(Collectors.toList());
    }

    private int getHitsCount(Map<String, Integer> hitsByIp, boolean unique) {
        if (unique) {
            return hitsByIp.size();
        }
        return hitsByIp
                .values()
                .stream()
                .mapToInt(Integer::intValue)
                .sum();
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
            return Objects.equals(app, hitKey.app) &&
                    Objects.equals(uri, hitKey.uri);
        }

        @Override
        public int hashCode() {
            return Objects.hash(app, uri);
        }
    }
}

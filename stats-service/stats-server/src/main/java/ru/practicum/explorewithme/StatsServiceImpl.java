package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
//@Slf4j
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;

    @Transactional
    @Override
    public ResponseEndpointHit save(RequestEndpointHit requestStatsDto) {
        EndpointHit endpointHit = endpointHitRepository.save(MapperEndpointHit.toEndpointHit(requestStatsDto));
        return MapperEndpointHit.toResponseEndpointHit(endpointHit);
    }

}

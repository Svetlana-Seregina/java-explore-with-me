package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.EndpointHitDto;
import ru.practicum.explorewithme.StatsClient;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class EndpointHitService {

    private final StatsClient statsClient;

    @Value("${app}")
    private String app;

    public void createEndpointHit(HttpServletRequest request) {
        String path = request.getRequestURI();
        log.info("endpoint path: {}", path);
        String ip = request.getRemoteAddr();
        log.info("client ip: {}", ip);

        EndpointHitDto endpointHitDto = new EndpointHitDto(app, path, ip, LocalDateTime.now());
        log.info("Передаем endpointHitDto в statsClient: {}", endpointHitDto);
        statsClient.save(endpointHitDto);
    }

}

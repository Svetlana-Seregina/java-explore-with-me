package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<Void> save(@RequestBody @Valid EndpointHitDto endpointHitDto) {
        log.info("Saving statistics: app = {}, uri = {}, ip = {}, timestamp = {}", endpointHitDto.getApp(),
                endpointHitDto.getUri(), endpointHitDto.getIp(), endpointHitDto.getTimestamp());
        statsService.save(endpointHitDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam(name = "start", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                    @RequestParam(name = "end", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                    @RequestParam(name = "uris", defaultValue = "") List<String> uris,
                                    @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        log.info("Get statistics: start = {}, end = {}, uris = {}, unique = {}", start, end, uris.size(), unique);
        log.info("Get uri: uris = {}", uris);
        List<ViewStats> viewStats = statsService.getStats(start, end, uris, unique);
        log.info("Получен ответ statsService.getStats(start, end, uris, unique); = {}", viewStats);
        return viewStats;
    }

}

package ru.practicum.explorewithme;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<Object> save(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Saving statistics: app = {}, uri = {}, ip = {}, timestamp = {}", endpointHitDto.getApp(),
                endpointHitDto.getUri(), endpointHitDto.getIp(), endpointHitDto.getTimestamp());
        statsService.save(endpointHitDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/stats")
    public List<ViewStats> getStats(@RequestParam(name = "start") String start,
                                    @RequestParam(name = "end") String end,
                                    @RequestParam(name = "uris", required = false) List<String> uris,
                                    @RequestParam(name = "unique", required = false, defaultValue = "false") boolean unique) {
        log.info("Get statistics: start = {}, end = {}, uris = {}, unique = {}", start, end, uris.size(), unique);
        log.info("Get uri: uris = {}", uris);
        log.info("Get uri: uris = {}", uris.get(0));
        return statsService.getStats(start, end, uris, unique);
    }

}

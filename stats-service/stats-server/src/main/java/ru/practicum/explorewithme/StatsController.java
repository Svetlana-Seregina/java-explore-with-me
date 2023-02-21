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
    public ResponseEntity<Object> save(@RequestBody RequestEndpointHit requestEndpointHit) {
        log.info("Saving statistics: app = {}, uri = {}, ip = {}, timestamp = {}", requestEndpointHit.getApp(),
                requestEndpointHit.getUri(), requestEndpointHit.getIp(), requestEndpointHit.getTimestamp());
        statsService.save(requestEndpointHit);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/stats")
    public List<ResponseViewStats> findAll(@RequestParam(name = "start") String start,
                                           @RequestParam(name = "end") String end,
                                           @RequestParam(name = "uris") List<String> uris,
                                           @RequestParam(name = "unique", defaultValue = "false") boolean unique) {
        log.info("Get statistics: start = {}, end = {}, uris = {}, unique = {}", start, end, uris.get(0), unique);
        return statsService.findAll(start, end, uris, unique);
    }

}

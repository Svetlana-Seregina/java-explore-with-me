package ru.practicum.explorewithme;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
@Slf4j
public class MapperEndpointHit {

    public static EndpointHit toEndpointHit(EndpointHitDto requestEndpointHit) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(requestEndpointHit.getApp());
        endpointHit.setUri(requestEndpointHit.getUri());
        endpointHit.setIp(requestEndpointHit.getIp());
        LocalDateTime timestamp = getLocalDateTime(requestEndpointHit.getTimestamp());
        endpointHit.setTimestamp(timestamp);
        return endpointHit;
    }

    public static ViewStats toViewStats(EndpointHit endpointHit) {
        String[] uriString = endpointHit.getUri().split("/");
        log.info("This is length of uriString {}", uriString.length);
        log.info("This is number[id] of events = {}", uriString[2]);
        return new ViewStats(
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getId().intValue() + 1
        );

    }

    public static LocalDateTime getLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

}

package ru.practicum.explorewithme;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class MapperEndpointHit {

    public static EndpointHit toEndpointHit(RequestEndpointHit requestEndpointHit) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(requestEndpointHit.getApp());
        endpointHit.setUri(requestEndpointHit.getUri());
        endpointHit.setIp(requestEndpointHit.getIp());
        LocalDateTime timestamp = getLocalDateTime(requestEndpointHit.getTimestamp());
        endpointHit.setTimestamp(timestamp);
        return endpointHit;
    }

    public static LocalDateTime getLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

}

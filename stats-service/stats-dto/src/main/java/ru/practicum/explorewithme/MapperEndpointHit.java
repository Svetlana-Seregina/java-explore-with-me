package ru.practicum.explorewithme;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class MapperEndpointHit {

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto, Application app) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(app);
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setIp(endpointHitDto.getIp());
        LocalDateTime timestamp = getLocalDateTime(endpointHitDto.getTimestamp());
        endpointHit.setTimestamp(timestamp);
        return endpointHit;
    }

    public static Application toApplication(String appName) {
        Application application = new Application();
        application.setName(appName);
        return application;
    }

    public static LocalDateTime getLocalDateTime(String date) {
        return LocalDateTime.parse(date, formatter);
    }
}

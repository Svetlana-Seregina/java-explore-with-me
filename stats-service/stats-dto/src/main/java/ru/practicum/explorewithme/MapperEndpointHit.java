package ru.practicum.explorewithme;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MapperEndpointHit {

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHitDto, Application app) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setApp(app);
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setTimestamp(endpointHitDto.getTimestamp());
        return endpointHit;
    }

    public static Application toApplication(String appName) {
        Application application = new Application();
        application.setName(appName);
        return application;
    }

}

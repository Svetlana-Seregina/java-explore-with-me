package ru.practicum.explorewithme;

import lombok.experimental.UtilityClass;

import java.sql.Timestamp;

@UtilityClass
public class MapperEndpointHit {

    public static EndpointHit toEndpointHit(RequestEndpointHit requestEndpointHit) {
        EndpointHit endpointHit =new EndpointHit();
        endpointHit.setApp(requestEndpointHit.getApp());
        endpointHit.setUri(requestEndpointHit.getUri());
        endpointHit.setIp(requestEndpointHit.getIp());
        var t = Timestamp.valueOf(requestEndpointHit.getTimestamp());
        var l = t.toLocalDateTime();
        endpointHit.setTimestamp(l);
        return endpointHit;
    }

    public static ResponseEndpointHit toResponseEndpointHit(EndpointHit endpointHit) {
        return new ResponseEndpointHit(
                endpointHit.getApp(),
                endpointHit.getUri()
        );
    }
}

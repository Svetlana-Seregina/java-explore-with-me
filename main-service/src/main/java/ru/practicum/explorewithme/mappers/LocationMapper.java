package ru.practicum.explorewithme.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.Location;

@UtilityClass
public class LocationMapper {

    public static Location toLocation(Double lat, Double lon) {
        Location location = new Location();
        location.setLat(lat);
        location.setLon(lon);
        return location;
    }

}

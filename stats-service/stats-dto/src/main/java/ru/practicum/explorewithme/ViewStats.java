package ru.practicum.explorewithme;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ViewStats {

    private final String app;
    private final String uri;
    private final Integer hits;

}

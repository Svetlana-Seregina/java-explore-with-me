package ru.practicum.explorewithme;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
public class ViewStats {

    private String app;
    private String uri;
    private Long hits;

}

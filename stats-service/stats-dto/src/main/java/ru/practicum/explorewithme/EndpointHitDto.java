package ru.practicum.explorewithme;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotBlank
    private String ip;
    @NotBlank
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}

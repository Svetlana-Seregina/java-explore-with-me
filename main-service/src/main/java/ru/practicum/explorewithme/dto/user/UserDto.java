package ru.practicum.explorewithme.dto.user;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDto {

    private final Long id;
    private final String name;
    private final String email;

}

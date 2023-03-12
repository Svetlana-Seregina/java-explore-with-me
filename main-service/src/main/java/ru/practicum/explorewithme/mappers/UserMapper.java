package ru.practicum.explorewithme.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;

@UtilityClass
public class UserMapper {

    public static UserDto toUserDto(NewUserRequest newUserRequest) {
        UserDto userDto = new UserDto();
        userDto.setName(newUserRequest.getName());
        userDto.setEmail(newUserRequest.getEmail());
        return userDto;
    }
}

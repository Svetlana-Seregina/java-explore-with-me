package ru.practicum.explorewithme.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.User;
import ru.practicum.explorewithme.dto.user.UserDto;

@UtilityClass
public class UserMapper {

    public static User toUser(NewUserRequest newUserRequest) {
        User user = new User();
        user.setName(newUserRequest.getName());
        user.setEmail(newUserRequest.getEmail());
        return user;
    }


    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}

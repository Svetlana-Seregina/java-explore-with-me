package ru.practicum.explorewithme.service.user;

import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAllUsers(List<Long> ids, Integer from, Integer size);

    UserDto addNewUser(NewUserRequest newUserRequest);

    boolean deleteUserById(Long userId);

}

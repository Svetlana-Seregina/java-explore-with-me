package ru.practicum.explorewithme.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.service.user.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserAdminController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> findAllUsers(@RequestParam(value = "ids") List<Long> ids,
                                      @RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабатываем запрос на поиск всех пользователей: количество = {}, from = {}, size = {}", ids.size(), from, size);
        return userService.findAllUsers(ids, from, size);
    }

    @PostMapping
    public ResponseEntity<UserDto> addNewUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Обрабатываем запрос на добавление пользователя: {}", newUserRequest);
        UserDto userDto = userService.addNewUser(newUserRequest);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Boolean> deleteUserById(@PathVariable long userId) {
        log.info("Обрабатываем запрос на удаление пользователя с id = {}", userId);
        boolean deleteUser = userService.deleteUserById(userId);
        log.info("Пользователь удален ? = {}", deleteUser);
        return new ResponseEntity<>(deleteUser, HttpStatus.NO_CONTENT);
    }

}

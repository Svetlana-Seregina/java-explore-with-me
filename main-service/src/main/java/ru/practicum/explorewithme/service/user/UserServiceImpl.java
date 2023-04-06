package ru.practicum.explorewithme.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.User;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.mappers.UserMapper;
import ru.practicum.explorewithme.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAllUsers(List<Long> ids, Integer from, Integer size) {
        if (!ids.isEmpty()) {
            List<User> users = userRepository.findAllById(ids);
            log.info("Найдено {} пользователей в userDtoList.", users.size());
            if (users.size() == 0) {
                return Collections.emptyList();
            }
            return users
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(toList());
        }

        Pageable pageable = PageRequest.of(from, size);
        List<User> userPageable = userRepository.findAll(pageable)
                .stream()
                .collect(Collectors.toList());
        log.info("Найдено {} пользователей в userPageable.", userPageable.size());

        if (userPageable.size() == 0) {
            return Collections.emptyList();
        }
        return userPageable
                .stream()
                .map(UserMapper::toUserDto)
                .collect(toList());
    }

    @Transactional
    @Override
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        User user = userRepository.save(UserMapper.toUser(newUserRequest));
        log.info("Создан пользователь {}", user);
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public boolean deleteUserById(Long userId) {
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {} удален.", userId);
        return userRepository.existsById(userId);
    }

}

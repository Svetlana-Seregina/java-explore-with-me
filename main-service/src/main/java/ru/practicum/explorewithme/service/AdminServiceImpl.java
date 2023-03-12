package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.UpdateEventAdminRequest;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.mappers.UserMapper;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> findUsers(List<Long> ids, Integer from, Integer size) {
        /*Pageable pageable =
                PageRequest.of(from, size);*/
        List<UserDto> userDtoList = userRepository.findAllById(ids);
        log.info("Найдено {} пользователей", userDtoList.size());
        if(userDtoList.size() == 0) {
            return Collections.emptyList();
        }
        return userDtoList;
    }

    @Transactional
    @Override
    public UserDto addNewUser(NewUserRequest newUserRequest) {
        UserDto userDto = userRepository.save(UserMapper.toUserDto(newUserRequest));
        log.info("Создан пользователь {}", userDto);
        return userDto;
    }

    @Transactional
    @Override
    public boolean deleteUser(Long userId) {
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {} удален.", userId);
        return userRepository.existsById(userId);
    }

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        return null;
    }

    @Override
    public boolean deleteById(long id) {
        return false;
    }

    @Override
    public void updateCategoryName(long id) {
    }

    @Override
    public EventFullDto findAllCategories(List<Integer> users, List<String> states, List<Integer> categories,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        return null;
    }

    @Override
    public void updateEventById(UpdateEventAdminRequest updateEventAdminRequest) {

    }


    @Override
    public void createNewCompilation(NewCompilationDto newCompilationDto) {

    }

    @Override
    public boolean deleteCompilation(long id) {
        return false;
    }

    @Override
    public CompilationDto updateCompilationById(UpdateCompilationRequest updateCompilationRequest) {
        return null;
    }


}

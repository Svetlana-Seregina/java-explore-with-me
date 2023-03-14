package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.UpdateEventAdminRequest;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {

    List<UserDto> findUsers(List<Long> ids, Integer from, Integer size);

    UserDto addNewUser(NewUserRequest newUserRequest);

    boolean deleteUser(Long userId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    boolean deleteById(long id);

    CategoryDto updateCategoryName(long id, NewCategoryDto newCategoryDto);

    EventFullDto findAllEvents(List<Integer> users, List<String> states, List<Integer> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    void updateEventById(UpdateEventAdminRequest updateEventAdminRequest);


    void createNewCompilation(NewCompilationDto newCompilationDto);

    boolean deleteCompilation(long id);

    CompilationDto updateCompilationById(UpdateCompilationRequest updateCompilationRequest);
}

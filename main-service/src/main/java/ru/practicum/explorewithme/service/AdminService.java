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

    List<UserDto> findAllUsers(List<Long> ids, Integer from, Integer size);

    UserDto addNewUser(NewUserRequest newUserRequest);

    boolean deleteUserById(Long userId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    boolean deleteCategoryById(long catId);

    CategoryDto updateCategoryName(long id, CategoryDto categoryDto);

    List<EventFullDto> findAllEvents(List<Long> users, List<String> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventById(UpdateEventAdminRequest updateEventAdminRequest, long eventId);

    CompilationDto createNewCompilation(NewCompilationDto newCompilationDto);

    boolean deleteCompilation(long compId);

    CompilationDto updateCompilationById(UpdateCompilationRequest updateCompilationRequest, long compId);

}

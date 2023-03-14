package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.UpdateEventAdminRequest;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.service.AdminService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public List<UserDto> findUsers(@RequestParam(value = "ids") List<Long> ids,
                                   @RequestParam(value = "from", defaultValue = "0") Integer from,
                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабатываем запрос на поиск всех пользователей: количество = {}, from = {}, size = {}", ids.size(), from, size);
        return adminService.findUsers(ids, from, size);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> addNewUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Обрабатываем запрос на добавление пользователя: {}", newUserRequest);
        UserDto userDto = adminService.addNewUser(newUserRequest);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Boolean> deleteUser(@PathVariable long userId) {
        log.info("Обрабатываем запрос на удаление пользователя с id = {}", userId);
        boolean deleteUser = adminService.deleteUser(userId);
        log.info("Пользователь удален ? = {}", deleteUser);
        return new ResponseEntity<>(deleteUser, HttpStatus.NO_CONTENT);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        CategoryDto categoryDto = adminService.createCategory(newCategoryDto);
        return new ResponseEntity<>(categoryDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Boolean> deleteById(@PathVariable long catId) {
        log.info("Создан запрос на удаление категории по id = {}", catId);
        boolean categoryDelete = adminService.deleteById(catId);
        return new ResponseEntity<>(categoryDelete, HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> updateCategoryName(@PathVariable long catId,
                                                          @RequestBody @Valid NewCategoryDto newCategoryDto) {
        CategoryDto categoryDto = adminService.updateCategoryName(catId, newCategoryDto);
        return new ResponseEntity<>(categoryDto, HttpStatus.OK);
    }


    @GetMapping("/events")
    public EventFullDto findAllEvents(@RequestParam(value = "users") List<Integer> users,
                                      @RequestParam(value = "states") List<String> states,
                                      @RequestParam(value = "categories") List<Integer> categories,
                                      @RequestParam(value = "rangeStart") LocalDateTime rangeStart,
                                      @RequestParam(value = "rangeEnd") LocalDateTime rangeEnd,
                                      @RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return adminService.findAllEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{id}")
    public void updateEventById(@RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        adminService.updateEventById(updateEventAdminRequest);
    }


    @PostMapping("/compilations")
    public void createNewCompilation(@RequestBody NewCompilationDto newCompilationDto) {
        adminService.createNewCompilation(newCompilationDto);
    }

    @DeleteMapping("/compilations/{id}")
    public boolean deleteCompilation(@PathVariable long id) {
        return adminService.deleteCompilation(id);
    }

    @PatchMapping("/compilations/{id}")
    public CompilationDto updateCompilationById(@RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return adminService.updateCompilationById(updateCompilationRequest);
    }

}

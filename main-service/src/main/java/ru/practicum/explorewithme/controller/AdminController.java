package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
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
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
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
    public List<UserDto> findAllUsers(@RequestParam(value = "ids") List<Long> ids,
                                      @RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабатываем запрос на поиск всех пользователей: количество = {}, from = {}, size = {}", ids.size(), from, size);
        return adminService.findAllUsers(ids, from, size);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDto> addNewUser(@RequestBody @Valid NewUserRequest newUserRequest) {
        log.info("Обрабатываем запрос на добавление пользователя: {}", newUserRequest);
        UserDto userDto = adminService.addNewUser(newUserRequest);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Boolean> deleteUserById(@PathVariable long userId) {
        log.info("Обрабатываем запрос на удаление пользователя с id = {}", userId);
        boolean deleteUser = adminService.deleteUserById(userId);
        log.info("Пользователь удален ? = {}", deleteUser);
        return new ResponseEntity<>(deleteUser, HttpStatus.NO_CONTENT);
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Обрабатываем запрос на создание категории. Данные для создания категории newCategoryDto = {}", newCategoryDto);
        CategoryDto categoryDto = adminService.createCategory(newCategoryDto);
        return new ResponseEntity<>(categoryDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/categories/{catId}")
    public ResponseEntity<Boolean> deleteCategoryById(@PathVariable long catId) {
        log.info("Обрабатываем запрос на удаление категории. Создан запрос на удаление категории по id = {}", catId);
        boolean categoryDelete = adminService.deleteCategoryById(catId);
        return new ResponseEntity<>(categoryDelete, HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> updateCategoryName(@PathVariable long catId,
                                                          @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Обрабатываем запрос на обновление категории. Редактирование категории по id = {}, categoryDto = {}", catId, categoryDto);
        CategoryDto category = adminService.updateCategoryName(catId, categoryDto);
        return new ResponseEntity<>(category, HttpStatus.OK);
    }


    @GetMapping("/events")
    public List<EventFullDto> findAllEvents(@RequestParam(required = false) List<Long> users,
                                            @RequestParam(required = false) List<String> states,
                                            @RequestParam(required = false) List<Long> categories,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                            @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабытываем запрос на поиск всех событий по заданным требованиям: users = {}; states = {}; " +
                        "categories = {}; rangeStart = {}; rangeEnd = {}; from = {}, size = {}", users, states, categories,
                rangeStart, rangeEnd, from, size);
        return adminService.findAllEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEventById(@PathVariable long eventId,
                                        @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Обрабатываем запрос на редактирование события. Данные для редактирования = {}", updateEventAdminRequest);
        return adminService.updateEventById(updateEventAdminRequest, eventId);
    }


    @PostMapping("/compilations")
    public ResponseEntity<CompilationDto> createNewCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Обрабатываем запрос на создание подборки = {}", newCompilationDto);
        CompilationDto compilationDto = adminService.createNewCompilation(newCompilationDto);
        return new ResponseEntity<>(compilationDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/compilations/{compId}")
    public ResponseEntity<Boolean> deleteCompilation(@PathVariable long compId) {
        log.info("Обрабатываем запрос на удаление подборки, id = {}", compId);
        boolean deleteCompilation = adminService.deleteCompilation(compId);
        return new ResponseEntity<>(deleteCompilation, HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilationById(@RequestBody UpdateCompilationRequest updateCompilationRequest,
                                                @PathVariable long compId) {
        log.info("Обрабатываем запрос на обновление подборки. Данные для обновления = {}", updateCompilationRequest);
        return adminService.updateCompilationById(updateCompilationRequest, compId);
    }

}

package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.StatsClient;
import ru.practicum.explorewithme.ViewStats;
import ru.practicum.explorewithme.dto.category.Category;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.dto.compilation.Compilation;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.request.ParticipationRequest;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.User;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.mappers.CategoryMapper;
import ru.practicum.explorewithme.mappers.CompilationMapper;
import ru.practicum.explorewithme.mappers.EventMapper;
import ru.practicum.explorewithme.mappers.UserMapper;
import ru.practicum.explorewithme.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatsClient statsClient;

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

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.save(CategoryMapper.toCategory(newCategoryDto));
        log.info("Содана новая категория = {}", category);
        return CategoryMapper.toCategoryDto(category);
    }

    @SneakyThrows
    @Transactional
    @Override
    public boolean deleteCategoryById(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категории с id = %d нет в базе.", catId)));
        List<Event> events = eventRepository.findAllByCategory(category);
        if (!events.isEmpty()) {
            throw new ValidationException(String.format("Категория не может быть удалена, т.к. с ней связаны события." +
                    " Количество событий = %d", events.size()));
        }
        categoryRepository.deleteById(catId);
        log.info("Категория с id = {} удалена.", catId);
        return categoryRepository.existsById(catId);
    }

    @Transactional
    @Override
    public CategoryDto updateCategoryName(long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категории с id = %d нет в базе.", id)));
        log.info("Найдена категория = {}", category);

        if (categoryDto.getName() != null && !categoryDto.getName().isBlank()) {
            String name = categoryDto.getName();
            if (categoryRepository.findByName(name).isPresent()) {
                throw new ValidationException(String.format("Категория с таким именем = %s уже существует в базе.", name));
            }
            category.setName(name);
            log.info("Имя категории изменено на {}, id = {}", category.getName(), category.getId());
        }
        return CategoryMapper.toCategoryDto(category);
    }
    //category.setName(category.getName());
    //return CategoryMapper.toCategoryDto(category);


    @Override
    public List<EventFullDto> findAllEvents(List<Long> users, List<String> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {

        List<User> userList = null;
        if (users != null) {
            userList = userRepository.findAllById(users);
            log.info("Найден пользователь = {}", userList);
        }

        List<EventState> listOfStates = null;
        if (states != null) {
            listOfStates = new ArrayList<>();
            for (String s : states) {
                EventState eventState = EventState.valueOf(s);
                log.info("Найден статус = {}", eventState);
                listOfStates.add(eventState);
            }
        }

        List<Category> categoryList = null;
        if (categories != null) {
            categoryList = categoryRepository.findAllById(categories);
            log.info("Найдена категория = {}", categoryList);
        }

        Pageable pageable = PageRequest.of(from, size);

        List<Event> eventsByQuery = eventRepository.findAllByQueryAdminParams(
                        userList, listOfStates, categoryList, rangeStart, rangeEnd, pageable)
                .stream()
                .collect(Collectors.toList());

        log.info("По заданным параметрам в базе найден список с событиями: eventsByQuery = {}", eventsByQuery);

        if (eventsByQuery.isEmpty()) {
            log.info("По заданным параметрам в базе ничего не найдено. Возвращаем пустой список.");
            return Collections.emptyList();
        }

        List<Long> eventIds = new ArrayList<>();
        for (Event event : eventsByQuery) {
            Long evId = event.getId();
            eventIds.add(evId);
        }

        Map<Long, List<ParticipationRequest>> confirmedRequestsByEventId = participationRequestRepository.findAllByEventId_In(eventIds)
                .stream()
                .collect(Collectors.groupingBy(b -> b.getEvent().getId(), toList()));
        log.info("confirmedRequestsByEventId = {}", confirmedRequestsByEventId.size());

        LocalDateTime publishedDate = eventsByQuery
                .stream()
                .min(Comparator.comparing(Event::getPublishedOn))
                .map(Event::getPublishedOn)
                .orElseThrow();

        log.info("Самая ранняя дата в списке publishedDate = {}", publishedDate);

        LocalDateTime actualDate = LocalDateTime.now();
        log.info("Даты для поиска события: самая ранняя дата в списке publishedDate = {}; текущая дата actualDate = {}",
                publishedDate, actualDate);

        List<String> uris = new ArrayList<>();
        for (Event event : eventsByQuery) {
            uris.add("/events/" + event.getId());
        }
        Map<Long, List<ViewStats>> views = statsClient.getStats(publishedDate, actualDate, uris, false)
                .stream()
                .collect(Collectors.groupingBy(b -> (long) Integer.parseInt(b.getUri().substring(8)), toList()));

        log.info("Получена статистика views = {}", views.entrySet());

        List<EventFullDto> eventFullDtos = eventsByQuery.stream()
                .map(EventMapper::toEventFullDto)
                .map(eventFullDto -> {
                    if (confirmedRequestsByEventId.isEmpty()) {
                        return eventFullDto;
                    }
                    List<ParticipationRequest> participationRequests = confirmedRequestsByEventId.get(eventFullDto.getId());
                    int confirmedRequests = participationRequests.size();
                    return EventMapper.toEventFullDto(eventFullDto, (long) confirmedRequests, 0L);
                }).map(eventFullDto -> {
                    List<ViewStats> viewStats = views.get(eventFullDto.getId());
                    Long allViews = 0L;
                    if (viewStats != null) {
                        allViews = views.get(eventFullDto.getId())
                                .stream()
                                .map(ViewStats::getHits)
                                .findFirst()
                                .get();
                        return EventMapper.toEventFullDtoWithViews(eventFullDto, allViews);
                    }
                    return EventMapper.toEventFullDtoWithViews(eventFullDto, allViews);
                })
                .collect(toList());
        log.info("Количество найденных событий по заданным требованиям eventFullDtos = {}", eventFullDtos);

        return eventFullDtos;
    }

    @SneakyThrows
    @Override
    @Transactional
    public EventFullDto updateEventById(UpdateEventAdminRequest updateEventAdminRequest, long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Событие по id = %d не найдено", eventId)));
        log.info("Найдено событие для редактирования = {}", event);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ValidationException("Обратите внимание: " +
                    "дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ValidationException("Обратите внимание: " +
                        "дата начала изменяемого события должна быть не ранее чем за час от даты публикации.");
            }
        }

        if (updateEventAdminRequest.getStateAction().equals("REJECT_EVENT") &&
                event.getState().equals(EventState.PENDING)) {
            event.setState(EventState.CANCELED);
            log.info("Данные события обновлены: state = {}", event.getState());
            return EventMapper.toEventFullDto(event);
        }

        if (updateEventAdminRequest.getStateAction().equals("REJECT_EVENT") &&
                event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Обратите внимание: событие в статусе PUBLISHED отменить нельзя.");
        }

        if (updateEventAdminRequest.getStateAction().equals("PUBLISH_EVENT") &&
                event.getState().equals(EventState.CANCELED)) {
            throw new ValidationException("Обратите внимание: событие в статусе CANCELED опубликовать нельзя.");
        }

        if (updateEventAdminRequest.getStateAction().equals("PUBLISH_EVENT") &&
                event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Обратите внимание: событие уже опубликовано.");
        }

        if (updateEventAdminRequest.getStateAction().equals("PUBLISH_EVENT") &&
                event.getState().equals(EventState.PENDING)) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
            int confirmedRequests = participationRequestRepository.findAllByEventId(eventId).size();
            event.setConfirmedRequests((long) confirmedRequests);
            log.info("Данные события обновлены: state = {}, publication = {}, confirmedRequests = {}", event.getState(), event.getPublishedOn(), event.getConfirmedRequests());
            event.setAnnotation(updateEventAdminRequest.getAnnotation() != null && !updateEventAdminRequest.getAnnotation().isBlank() ?
                    updateEventAdminRequest.getAnnotation() : event.getAnnotation());
            event.setPaid(updateEventAdminRequest.getPaid() != null ?
                    updateEventAdminRequest.getPaid() : event.getPaid());
            event.setDescription(updateEventAdminRequest.getDescription() != null && !updateEventAdminRequest.getDescription().isBlank() ?
                    updateEventAdminRequest.getDescription() : event.getDescription());
            event.setTitle(updateEventAdminRequest.getTitle() != null && !updateEventAdminRequest.getTitle().isBlank() ?
                    updateEventAdminRequest.getTitle() : event.getTitle());
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit() != null ?
                    updateEventAdminRequest.getParticipantLimit() : event.getParticipantLimit());
            event.setEventDate(updateEventAdminRequest.getEventDate() != null ?
                    updateEventAdminRequest.getEventDate() : event.getEventDate());

            log.info("Событие обновлено = {}", event);
            return EventMapper.toEventFullDto(event, (long) confirmedRequests, 0L);
        }

        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
        List<Long> eventIds = newCompilationDto.getEvents();
        if (eventIds.isEmpty()) {
            Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, Collections.emptyList()));
            log.info("Создана подборка {}", compilation);
            CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation, Collections.emptyList());
            log.info("Количество событий в compilationDto: {}", compilationDto.getEvents().size());
            return compilationDto;
        }

        log.info("КОЛИЧЕСТВО --->> eventIds = {}", eventIds.size());
        List<Event> events = eventRepository.findAllById(eventIds);

        log.info("Количество events = {}", events.size());
        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events));
        log.info("Создана подборка {}", compilation);
        List<EventShortDto> eventShortDtoList = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        log.info("Количество eventShortDtoList = {}", eventShortDtoList.size());
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
        log.info("Количество событий в compilationDto: {}", compilationDto.getEvents().size());
        return compilationDto;
    }

    @Transactional
    @Override
    public boolean deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
        log.info("Подборка с id = {} удалена.", compId);
        boolean delete = compilationRepository.existsById(compId);
        log.info("Подборка есть в базе? = {}", delete);
        return delete;
    }

    @Transactional
    @Override
    public CompilationDto updateCompilationById(UpdateCompilationRequest updateCompilationRequest, long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Подборки с id = %d не найдено.", compId)));
        log.info("Найдена подборка в базе по id: {} до внесения изменений", compilation);

        List<Long> eventIds = updateCompilationRequest.getEvents();
        log.info("Количество eventIds: {}", eventIds);

        if (eventIds.isEmpty()) {
            Set<Event> eventSet = new HashSet<>();
            compilation.setEvents(eventSet);
            compilation.setPinned(updateCompilationRequest.getPinned());
            compilation.setTitle(updateCompilationRequest.getTitle());
            log.info("Создана подборка {}", compilation);
            CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation, Collections.emptyList());
            log.info("Количество событий в compilationDto: {}", compilationDto.getEvents().size());
            return compilationDto;
        }

        List<Event> events = eventRepository.findAllById(eventIds);
        log.info("Количество events: {}", events);

        Set<Event> eventSet = new HashSet<>(events);

        compilation.setEvents(eventSet);
        compilation.setPinned(updateCompilationRequest.getPinned());
        compilation.setTitle(updateCompilationRequest.getTitle());

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        log.info("Количество eventShortDtoList: {}", eventShortDtoList);

        log.info("Подборка после внесения изменений: {}", compilation);

        return CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
    }

}

package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.category.NewCategoryDto;
import ru.practicum.explorewithme.dto.compilation.Compilation;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.user.NewUserRequest;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.mappers.CategoryMapper;
import ru.practicum.explorewithme.mappers.CompilationMapper;
import ru.practicum.explorewithme.mappers.EventMapper;
import ru.practicum.explorewithme.mappers.UserMapper;
import ru.practicum.explorewithme.repository.*;

import javax.persistence.EntityNotFoundException;
import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public List<UserDto> findAllUsers(List<Long> ids, Integer from, Integer size) {
        if (!ids.isEmpty()) {
            List<UserDto> userDtoList = userRepository.findAllById(ids);
            log.info("Найдено {} пользователей в userDtoList.", userDtoList.size());
            if (userDtoList.size() == 0) {
                return Collections.emptyList();
            }
            return userDtoList;
        }

        Pageable pageable = PageRequest.of(from, size);
        List<UserDto> userPageable = userRepository.findAll(pageable)
                .stream()
                .collect(Collectors.toList());
        log.info("Найдено {} пользователей в userPageable.", userPageable.size());

        if (userPageable.size() == 0) {
            return Collections.emptyList();
        }
        return userPageable;
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
    public boolean deleteUserById(Long userId) {
        userRepository.deleteById(userId);
        log.info("Пользователь с id = {} удален.", userId);
        return userRepository.existsById(userId);
    }

    @Transactional
    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        CategoryDto category = categoryRepository.save(CategoryMapper.toCategoryDto(newCategoryDto));
        log.info("Содана новая категория = {}", category);
        return category;
    }

    @SneakyThrows
    @Transactional
    @Override
    public boolean deleteCategoryById(long catId) {
        CategoryDto categoryDto = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категории с id = %d нет в базе.", catId)));
        List<Event> events = eventRepository.findAllByCategory(categoryDto);
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
        CategoryDto category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категории с id = %d нет в базе.", id)));
        log.info("Найдена категория = {}", category);

        if (categoryDto.getName() != null && !categoryDto.getName().isBlank()) {
            String name = categoryDto.getName();
            if (categoryRepository.findByName(name).isPresent()) {
                throw new RuntimeException(String.format("Категория с таким именем = %s уже существует в базе.", name));
            }
            category.setName(name);
            log.info("Имя категории изменено на {}, id = {}", category.getName(), category.getId());
            return category;
        }
        category.setName(category.getName());
        return category;
    }

    @Override
    public List<EventFullDto> findAllEvents(List<Long> users, List<String> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        List<UserDto> userDtoList = userRepository.findAllById(users);
        log.info("Найдены пользователи = {}", userDtoList);
        List<CategoryDto> categoryDtoList = categoryRepository.findAllById(categories);
        log.info("Найдены категории = {}", categoryDtoList);

        List<EventState> listOfStates = new ArrayList<>();
        if(states != null) {
            for (String s : states) {
                EventState eventState = EventState.valueOf(s);
                listOfStates.add(eventState);
            }
        }

        Pageable pageable = PageRequest.of(from, size);
        List<Event> allEvents =
                eventRepository.findAllByInitiatorInAndStateInAndEventDateIsAfterAndEventDateIsBefore(
                        userDtoList, listOfStates, rangeStart, rangeEnd, pageable)
                        .stream()
                        .collect(Collectors.toList());
        log.info("Количество найденных событий по заданным требованиям allEvents = {}", allEvents);

        if (allEvents.isEmpty()) {
            log.info("По заданным параметрам в базе ничего не найдено. Возвращаем пустой список.");
            return Collections.emptyList();
        }
        List<EventFullDto> eventFullDtos = allEvents.stream()
                .map(EventMapper::toEventFullDto)
                .collect(Collectors.toList());
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
            return EventMapper.toEventFullDto(event, (long) confirmedRequests);
        }

        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
        List<Long> eventIds = newCompilationDto.getEvents();
        log.info("КОЛИЧЕСТВО --->> eventIds = {}", eventIds.size());
        List<Event> events = eventRepository.findAllById(eventIds);

        log.info("Количество events = {}", events.size());
        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events));
        log.info("Создана подборка с id = {}", compilation.getId());
        log.info("Полные данные подборки {}", compilation);
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

    private LocalDateTime getLocalDateTime(String date) {
        return LocalDateTime.parse(date, formatter);
    }


}

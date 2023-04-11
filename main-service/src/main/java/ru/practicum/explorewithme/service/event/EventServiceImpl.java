package ru.practicum.explorewithme.service.event;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.StatsClient;
import ru.practicum.explorewithme.ViewStats;
import ru.practicum.explorewithme.dto.Location;
import ru.practicum.explorewithme.dto.category.Category;
import ru.practicum.explorewithme.dto.comment.Comment;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.event.UpdateEventUserRequest.StateAction;
import ru.practicum.explorewithme.dto.request.EventRequestStatus;
import ru.practicum.explorewithme.dto.request.ParticipationRequest;
import ru.practicum.explorewithme.dto.user.User;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.mappers.EventMapper;
import ru.practicum.explorewithme.mappers.LocationMapper;
import ru.practicum.explorewithme.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toList;
import static ru.practicum.explorewithme.dto.event.UpdateEventAdminRequest.StateAction.PUBLISH_EVENT;
import static ru.practicum.explorewithme.dto.event.UpdateEventAdminRequest.StateAction.REJECT_EVENT;
import static ru.practicum.explorewithme.dto.event.UpdateEventUserRequest.StateAction.SEND_TO_REVIEW;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final CommentRepository commentRepository;
    private final StatsClient statsClient;

    @SneakyThrows
    @Transactional
    @Override
    public EventFullDto createNewEvent(long userId, NewEventDto newEventDto) {
        User user = findUserInRepository(userId);
        double lat = newEventDto.getLocation().getLat();
        double lon = newEventDto.getLocation().getLon();
        log.info("Местоположение: широта = {}, долгота = {}", lat, lon);
        Location location = locationRepository.save(LocationMapper.toLocation(lat, lon));
        log.info("Создана новая локация: {}", location);

        LocalDateTime eventDate = newEventDto.getEventDate();
        LocalDateTime localDateTimePlusTwoHours = LocalDateTime.now().plusHours(2);
        if (eventDate.isBefore(localDateTimePlusTwoHours)) {
            throw new ValidationException("Обратите внимание: дата и время на которые намечено событие не может быть раньше, " +
                    "чем через два часа от текущего момента");
        }

        Long catId = newEventDto.getCategory();
        Category category = findCategoryInRepository(catId);

        Event event = eventRepository.save(EventMapper.toEvent(category, user, location, newEventDto, EventState.PENDING));
        log.info("Создано новое событие в базе: {}", event);
        return EventMapper.toEventFullDto(event, 0L, 0L);
    }

    @Override
    public List<EventShortDto> findAllEventsByInitiator(long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        User user = findUserInRepository(userId);
        List<Event> allEvents = eventRepository.findAllByInitiator(user, pageable)
                .stream().collect(Collectors.toList());

        if (allEvents.size() == 0) {
            return Collections.emptyList();
        }

        return allEvents.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto findEventByInitiator(long userId, long eventId) {
        User user = findUserInRepository(userId);
        Event event = eventRepository.findByIdAndInitiator(eventId, user);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto findEventById(long id, String path) {
        Event event = findEventInRepository(id);
        Long confirmedRequests = participationRequestRepository.countAllByEventIdAndStatusIs(
                id, EventRequestStatus.CONFIRMED);
        log.info("confirmedRequests = {}", confirmedRequests);
        Long views = findViews(event, path);

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event, confirmedRequests, views);
        log.info("Найдено событие по id = {}; EVENT = {}", id, eventFullDto);

        return eventFullDto;
    }

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

        List<Category> categoryList = findAllCategories(categories);

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
        Map<Long, Long> confirmedRequestsByEventId = findConfirmedRequests(eventsByQuery);
        Map<Long, Long> views = findViewStats(eventsByQuery);

        List<EventFullDto> eventFullDtos = eventsByQuery.stream()
                .map(EventMapper::toEventFullDto)
                .map(eventFullDto -> {
                    Long confirmedRequests = confirmedRequestsByEventId.getOrDefault(eventFullDto.getId(), 0L);
                    return EventMapper.toEventFullDto(eventFullDto, confirmedRequests, 0L);
                })
                .map(eventFullDto -> {
                    Long allViews = views.getOrDefault(eventFullDto.getId(), 0L);
                    return EventMapper.toEventFullDtoWithViews(eventFullDto, allViews);
                })
                .collect(toList());
        log.info("Количество найденных событий по заданным требованиям eventFullDtos = {}", eventFullDtos);

        return eventFullDtos;
    }

    @Override
    public List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                             String sort, Integer from, Integer size,
                                             String path) {

        List<Category> categoryList = findAllCategories(categories);

        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = new ArrayList<>();
        if (rangeStart != null) {
            events = eventRepository.findAllByQueryPublicParams(
                            text, categoryList, paid, rangeStart, rangeEnd, EventState.PUBLISHED, pageable)
                    .stream()
                    .collect(toList());
            log.info("Найдены allEventsByQuery where rangeStart != null: {}", events);
        }
        if (rangeStart == null) {
            events = eventRepository.findAllByQueryPublicParams(
                            text, categoryList, paid, LocalDateTime.now(), rangeEnd, EventState.PUBLISHED, pageable)
                    .stream()
                    .collect(toList());
            log.info("Найдены allEventsByQuery where rangeStart == null: {}", events);
        }

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> confirmedRequestsByEventId = findConfirmedRequests(events);
        Map<Long, Long> views = findViewStats(events);
        Map<Long, Long> comments = findComments(events);

        List<EventShortDto> eventShortDtoList = toEventShortDtoList(events, confirmedRequestsByEventId, views, comments);
        log.info("eventShortDtoList = {}", eventShortDtoList);

        if (sort.equals("VIEWS")) {
            return eventShortDtoList.stream()
                    .sorted(Comparator.comparing(EventShortDto::getViews))
                    .collect(toList());
        }

        List<EventShortDto> eventShortDtos = eventShortDtoList.stream()
                .sorted(Comparator.comparing(EventShortDto::getEventDate))
                .collect(toList());
        log.info("eventShortDtos = {}", eventShortDtos);
        return eventShortDtos;
    }

    @SneakyThrows
    @Override
    @Transactional
    public EventFullDto updateEventById(UpdateEventAdminRequest updateEventAdminRequest, long eventId) {

        Event event = findEventInRepository(eventId);

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

        if (updateEventAdminRequest.getStateAction() == REJECT_EVENT &&
                event.getState().equals(EventState.PENDING)) {
            event.setState(EventState.CANCELED);
            log.info("Данные события обновлены: state = {}", event.getState());
            return EventMapper.toEventFullDto(event);
        }

        if (updateEventAdminRequest.getStateAction() == REJECT_EVENT &&
                event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Обратите внимание: событие в статусе PUBLISHED отменить нельзя.");
        }

        if (updateEventAdminRequest.getStateAction() == PUBLISH_EVENT &&
                event.getState().equals(EventState.CANCELED)) {
            throw new ValidationException("Обратите внимание: событие в статусе CANCELED опубликовать нельзя.");
        }

        if (updateEventAdminRequest.getStateAction() == PUBLISH_EVENT &&
                event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Обратите внимание: событие уже опубликовано.");
        }

        if (updateEventAdminRequest.getStateAction() == PUBLISH_EVENT &&
                event.getState().equals(EventState.PENDING)) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
            log.info("Данные события обновлены: state = {}, publication = {}",
                    event.getState(), event.getPublishedOn());
            setNewParameters(event, updateEventAdminRequest);

            log.info("Событие обновлено = {}", event);
            return EventMapper.toEventFullDto(event, 0L, 0L);
        }

        return EventMapper.toEventFullDto(event);
    }

    @SneakyThrows
    @Transactional
    @Override
    public EventFullDto updateEventByInitiator(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        User user = findUserInRepository(userId);
        Event event = findEventInRepository(eventId);

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Обратите внимание event: " +
                    "изменить можно только отмененные события или события в состоянии ожидания модерации.");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("Обратите внимание event: дата и время на которые намечено событие " +
                    "не может быть раньше, чем через два часа от текущего момента.");
        }

        if (updateEventUserRequest.getEventDate() != null) {
            if (updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Обратите внимание updateEventUserRequest: дата и время на которые намечено событие " +
                        "не может быть раньше, чем через два часа от текущего момента.");
            }
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction() ==
                    StateAction.CANCEL_REVIEW) {
                if (!event.getInitiator().equals(user)) {
                    throw new ValidationException("Обратите внимание: событие может быть отменено только текущим пользователем.");
                }
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ValidationException("Обратите внимание: " +
                            "отменить можно только отмененные события или события в состоянии ожидания модерации.");
                }

                event.setState(EventState.CANCELED);
                EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
                log.info("Событие обновлено {}", eventFullDto);
                return eventFullDto;

            }
            if (updateEventUserRequest.getStateAction() ==
                    SEND_TO_REVIEW) {
                event.setAnnotation(updateEventUserRequest.getAnnotation() != null && !updateEventUserRequest.getAnnotation().isBlank() ?
                        updateEventUserRequest.getAnnotation() : event.getAnnotation());
                if (updateEventUserRequest.getCategory() != null) {
                    Category category = findCategoryInRepository(updateEventUserRequest.getCategory());
                    event.setCategory(category);
                }

                setNewParameters(event, updateEventUserRequest);
                event.setState(EventState.PENDING);
                EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
                log.info("Событие обновлено {}", eventFullDto);
                return eventFullDto;
            }
        }
        return EventMapper.toEventFullDto(event);
    }

    private User findUserInRepository(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("Найден пользователь = {}", user);
        return user;
    }

    private Category findCategoryInRepository(long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категории с id = %d нет в базе", catId)));
        log.info("Найдена категория = {}", category);
        return category;
    }

    private Event findEventInRepository(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("События с id = %d в базе нет.", eventId)));
        log.info("Найдено событие, event = {}", event);
        return event;
    }

    private List<Category> findAllCategories(List<Long> categories) {
        List<Category> categoryList = null;
        if (categories != null) {
            categoryList = categoryRepository.findAllById(categories);
            log.info("Найдена категория = {}", categoryList);
        }
        return categoryList;
    }

    private Map<Long, Long> findConfirmedRequests(List<Event> events) {

        log.info("Поиск подтвержденных заявок на учатстие.");

        List<Long> eventIds = new ArrayList<>();
        for (Event event : events) {
            Long evId = event.getId();
            eventIds.add(evId);
        }
        log.info("Список eventIds для поиска = {}", eventIds.size());

        List<ParticipationRequest> confirmedRequests =
                participationRequestRepository.findAllByEventIdInAndStatusIs(eventIds, EventRequestStatus.CONFIRMED);

        log.info("Размер списка confirmedRequests = {}", confirmedRequests.size());

        if (confirmedRequests.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, Long> confirmedRequestsByEventId =
                confirmedRequests
                        .stream()
                        .collect(Collectors.groupingBy(b -> b.getEvent().getId(), counting()));
        log.info("Найдены confirmedRequestsByEventId = {}", confirmedRequestsByEventId.entrySet());

        return confirmedRequestsByEventId;
    }

    private Map<Long, Long> findComments(List<Event> events) {

        log.info("Поиск комментариев к событию.");

        List<Long> eventIds = new ArrayList<>();
        for (Event event : events) {
            Long evId = event.getId();
            eventIds.add(evId);
        }
        log.info("Список eventIds для поиска = {}", eventIds.size());

        List<Comment> comments = commentRepository.findAllById(eventIds);

        log.info("Размер списка comments = {}", comments.size());

        if (comments.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, Long> commentsByEventId = comments
                .stream()
                .collect(Collectors.groupingBy(b -> b.getEvent().getId(), counting()));
        log.info("Найдены commentsByEventId = {}", commentsByEventId);

        return commentsByEventId;
    }


    private Map<Long, Long> findViewStats(List<Event> events) {
        log.info("Поиск статистики просмотров.");

        LocalDateTime publishedDate = events
                .stream()
                .min(Comparator.comparing(Event::getPublishedOn))
                .map(Event::getPublishedOn)
                .orElseThrow();

        log.info("Самая ранняя дата в списке publishedDate = {}", publishedDate);

        LocalDateTime actualDate = LocalDateTime.now();
        log.info("Даты для поиска события: самая ранняя дата в списке publishedDate = {}; текущая дата actualDate = {}",
                publishedDate, actualDate);

        List<String> uris = new ArrayList<>();
        for (Event event : events) {
            uris.add("/events/" + event.getId());
        }
        log.info("СПИСОК URIS = {}", uris);

        Map<Long, Long> views = statsClient.getStats(publishedDate, actualDate, uris, true)
                .stream()
                .collect(Collectors.toMap(b -> (long) Integer.parseInt(b.getUri().substring(8)), ViewStats::getHits));

        log.info("Получена статистика views = {}", views.entrySet());

        return views;
    }

    private void setNewParameters(Event event, UpdateEventRequest updateEventRequest) {
        event.setDescription(updateEventRequest.getDescription() != null && !updateEventRequest.getDescription().isBlank() ?
                updateEventRequest.getDescription() : event.getDescription());
        event.setAnnotation(updateEventRequest.getAnnotation() != null && !updateEventRequest.getAnnotation().isBlank() ?
                updateEventRequest.getAnnotation() : event.getAnnotation());
        event.setEventDate(updateEventRequest.getEventDate() != null ?
                updateEventRequest.getEventDate() : event.getEventDate());
        event.setPaid(updateEventRequest.getPaid() != null ?
                updateEventRequest.getPaid() : event.getPaid());
        event.setTitle(updateEventRequest.getTitle() != null && !updateEventRequest.getTitle().isBlank() ?
                updateEventRequest.getTitle() : event.getTitle());
        event.setParticipantLimit(updateEventRequest.getParticipantLimit() != null ?
                updateEventRequest.getParticipantLimit() : event.getParticipantLimit());
        event.setRequestModeration(updateEventRequest.getRequestModeration() != null ?
                updateEventRequest.getRequestModeration() : event.getRequestModeration());
    }

    private Long findViews(Event event, String path) {
        log.info("Поиск статистики просмотров события.");
        LocalDateTime publishedDate = event.getPublishedOn();
        LocalDateTime actualDate = LocalDateTime.now();
        log.info("Даты для поиска статистики о событии от publishedDate = {} до actualDate = {}", publishedDate, actualDate);

        List<String> uris = new ArrayList<>();
        uris.add(path);
        List<ViewStats> viewStats = statsClient.getStats(publishedDate, actualDate, uris, true);
        log.info("Найдены просмотры события views = {}", viewStats.size());
        Long views = null;
        if (viewStats.size() > 0) {
            views = viewStats.get(0).getHits();
        }
        if (viewStats.size() == 0) {
            views = 0L;
        }
        return views;
    }

    private List<EventShortDto> toEventShortDtoList(List<Event> events,
                                                    Map<Long, Long> confirmedRequestsByEventId,
                                                    Map<Long, Long> views,
                                                    Map<Long, Long> comments) {

        return events.stream()
                .map(EventMapper::toEventShortDto)
                .map(eventShortDto -> {
                    Long confirmedRequests = confirmedRequestsByEventId.getOrDefault(eventShortDto.getId(), 0L);
                    return EventMapper.toEventShortDtoWithConfirmedRequests(eventShortDto, confirmedRequests);
                })
                .map(eventShortDto -> {
                    Long allViews = views.getOrDefault(eventShortDto.getId(), 0L);
                    return EventMapper.toEventShortDtoWithViews(eventShortDto, allViews);
                })
                .map(eventShortDto -> {
                    Long allComments = comments.getOrDefault(eventShortDto.getId(), 0L);
                    return EventMapper.toEventShortDtoWithComments(eventShortDto, allComments);
                })
                .collect(toList());
    }

}

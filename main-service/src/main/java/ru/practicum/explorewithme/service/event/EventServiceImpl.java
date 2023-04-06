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
        return EventMapper.toEventFullDto(event);
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

        List<ParticipationRequest> participationRequests = participationRequestRepository.findAllByEventIdAndStatusIs(
                id, EventRequestStatus.CONFIRMED);

        int confirmedRequests = participationRequests.size();

        LocalDateTime publishedDate = event.getPublishedOn();
        LocalDateTime actualDate = LocalDateTime.now();
        log.info("Даты для поиска статистики о событии от publishedDate = {} до actualDate = {}", publishedDate, actualDate);

        List<String> uris = new ArrayList<>();
        uris.add(path);
        List<ViewStats> viewStats = statsClient.getStats(publishedDate, actualDate, uris, false);
        log.info("Найдены просмотры события views = {}", viewStats.size());
        Long views = null;
        if (viewStats.size() > 0) {
            views = viewStats.get(0).getHits();
        }
        if (viewStats.size() == 0) {
            views = 0L;
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event, (long) confirmedRequests, views);
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
        Map<Long, List<ParticipationRequest>> confirmedRequestsByEventId = findConfirmedRequests(eventsByQuery);
        Map<Long, List<ViewStats>> views = findViewStats(eventsByQuery);

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

        Map<Long, List<ParticipationRequest>> confirmedRequestsByEventId = findConfirmedRequests(events);
        Map<Long, List<ViewStats>> views = findViewStats(events);

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(EventMapper::toEventShortDto)
                .map(eventShortDto -> {
                    if (confirmedRequestsByEventId.isEmpty()) {
                        return eventShortDto;
                    }
                    List<ParticipationRequest> participationRequests = confirmedRequestsByEventId.get(eventShortDto.getId());
                    int confirmedRequests = participationRequests.size();

                    return EventMapper.toEventShortDtoWithConfirmedRequests(eventShortDto, (long) confirmedRequests);
                })
                .map(eventShortDto -> {
                    List<ViewStats> viewStats = views.get(eventShortDto.getId());
                    log.info("viewStats = {}", viewStats);
                    Long hits = 0L;
                    if (viewStats != null) {
                        hits = viewStats.get(0).getHits();
                        log.info("hits = {}", hits);
                        return EventMapper.toEventShortDtoWithViews(eventShortDto, hits);
                    }
                    return EventMapper.toEventShortDtoWithViews(eventShortDto, hits);
                })
                .collect(toList());

        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                return eventShortDtoList.stream()
                        .sorted(Comparator.comparing(EventShortDto::getEventDate))
                        .collect(toList());
            }
            if (sort.equals("VIEWS")) {
                return eventShortDtoList.stream()
                        .sorted(Comparator.comparing(EventShortDto::getViews))
                        .collect(toList());
            }
        }
        log.info("eventShortDtoList = {}", eventShortDtoList);
        return eventShortDtoList;
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

        if (UpdateEventAdminRequest.StateAction.valueOf(updateEventAdminRequest.getStateAction()) == REJECT_EVENT &&
                event.getState().equals(EventState.PENDING)) {
            event.setState(EventState.CANCELED);
            log.info("Данные события обновлены: state = {}", event.getState());
            return EventMapper.toEventFullDto(event);
        }

        if (UpdateEventAdminRequest.StateAction.valueOf(updateEventAdminRequest.getStateAction()) == REJECT_EVENT &&
                event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Обратите внимание: событие в статусе PUBLISHED отменить нельзя.");
        }

        if (UpdateEventAdminRequest.StateAction.valueOf(updateEventAdminRequest.getStateAction()) == PUBLISH_EVENT &&
                event.getState().equals(EventState.CANCELED)) {
            throw new ValidationException("Обратите внимание: событие в статусе CANCELED опубликовать нельзя.");
        }

        if (UpdateEventAdminRequest.StateAction.valueOf(updateEventAdminRequest.getStateAction()) == PUBLISH_EVENT &&
                event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Обратите внимание: событие уже опубликовано.");
        }

        if (UpdateEventAdminRequest.StateAction.valueOf(updateEventAdminRequest.getStateAction()) == PUBLISH_EVENT &&
                event.getState().equals(EventState.PENDING)) {
            event.setState(EventState.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
            int confirmedRequests = participationRequestRepository.findAllByEventIdAndStatusIs(eventId, EventRequestStatus.CONFIRMED).size();
            log.info("Данные события обновлены: state = {}, publication = {}, confirmedRequests = {}",
                    event.getState(), event.getPublishedOn(), confirmedRequests);
            setNewParameters(event, updateEventAdminRequest);

            log.info("Событие обновлено = {}", event);
            return EventMapper.toEventFullDto(event, (long) confirmedRequests, 0L);
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
            if (StateAction.valueOf(updateEventUserRequest.getStateAction()) ==
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
            if (StateAction.valueOf(updateEventUserRequest.getStateAction()) ==
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

    private Map<Long, List<ParticipationRequest>> findConfirmedRequests(List<Event> events) {
        List<Long> eventIds = new ArrayList<>();
        for (Event event : events) {
            Long evId = event.getId();
            eventIds.add(evId);
        }

        Map<Long, List<ParticipationRequest>> confirmedRequestsByEventId =
                participationRequestRepository.findAllByEventIdInAndStatusIs(eventIds, EventRequestStatus.CONFIRMED)
                        .stream()
                        .collect(Collectors.groupingBy(b -> b.getEvent().getId(), toList()));
        log.info("confirmedRequestsByEventId.size = {}", confirmedRequestsByEventId.size());

        return confirmedRequestsByEventId;
    }

    private Map<Long, List<ViewStats>> findViewStats(List<Event> events) {

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

        Map<Long, List<ViewStats>> views = statsClient.getStats(publishedDate, actualDate, uris, false)
                .stream()
                .collect(Collectors.groupingBy(b -> (long) Integer.parseInt(b.getUri().substring(8)), toList()));

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

}

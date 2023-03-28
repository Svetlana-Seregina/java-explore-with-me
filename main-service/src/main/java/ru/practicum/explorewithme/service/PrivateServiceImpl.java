package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.Location;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.request.*;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mappers.EventMapper;
import ru.practicum.explorewithme.mappers.LocationMapper;
import ru.practicum.explorewithme.mappers.ParticipationRequestMapper;
import ru.practicum.explorewithme.repository.*;

import javax.xml.bind.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PrivateServiceImpl implements PrivateService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    @Override
    public List<EventShortDto> findAllEventsByInitiator(long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        UserDto userDto = findUserInRepository(userId);
        List<Event> allEvents = eventRepository.findAllByInitiator(userDto, pageable)
                .stream().collect(Collectors.toList());

        if (allEvents.size() == 0) {
            return Collections.emptyList();
        }

        return allEvents.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Transactional
    @Override
    public EventFullDto createNewEvent(long userId, NewEventDto newEventDto) {
        UserDto userDto = findUserInRepository(userId);
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
        CategoryDto categoryDto = findCategoryInRepository(catId);

        Event event = eventRepository.save(EventMapper.toEvent(categoryDto, userDto, location, newEventDto, EventState.PENDING));
        log.info("Создано новое событие в базе: {}", event);
        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto findEventByInitiator(long userId, long eventId) {
        UserDto user = findUserInRepository(userId);
        Event event = eventRepository.findByIdAndInitiator(eventId, user);
        return EventMapper.toEventFullDto(event);
    }

    @SneakyThrows
    @Transactional
    @Override
    public EventFullDto updateEventByInitiator(long userId, long eventId, UpdateEventUserRequest updateEventUserRequest) {
        UserDto userDto = findUserInRepository(userId);

        Event event = findEventInRepository(eventId);

        if (updateEventUserRequest.getStateAction().equals("CANCEL_REVIEW")) {
            if (!event.getInitiator().equals(userDto)) {
                throw new ValidationException("Обратите внимание: событие может быть отменено только текущим пользователем.");
            }
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new ValidationException("Обратите внимание: " +
                        "отменить можно только отмененные события или события в состоянии ожидания модерации.");
            }
            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("Обратите внимание: дата и время на которые намечено событие " +
                        "не может быть раньше, чем через два часа от текущего момента.");
            }

            event.setState(EventState.CANCELED);
            EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
            log.info("Событие обновлено {}", eventFullDto);
            return eventFullDto;

        }
        event.setState(EventState.PENDING);
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event);
        log.info("Событие обновлено {}", eventFullDto);
        return eventFullDto;
    }

    @SneakyThrows
    @Override
    public List<ParticipationRequestDto> findAllEventRequestsByInitiator(long userId, long eventId) {
        UserDto userDto = findUserInRepository(userId);
        Event event = findEventInRepository(eventId);

        if (!event.getInitiator().equals(userDto)) {
            throw new ValidationException("Поиск запросов на участие может быть выполнен только организатором события.");
        }

        List<ParticipationRequest> participationRequestList = participationRequestRepository.findAllByEventId(eventId);

        List<ParticipationRequestDto> participationRequestDtos = participationRequestList.stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        log.info("Количество найденных запросов на участие participationRequestDtos = {}", participationRequestDtos.size());

        return participationRequestDtos;
    }

    @SneakyThrows
    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatusByInitiator(
            long userId, long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        UserDto userDto = findUserInRepository(userId);
        Event event = findEventInRepository(eventId);

        if (!event.getInitiator().equals(userDto)) {
            throw new ValidationException("Обновление запросов на участие может быть выполнено только организатором события.");
        }

        List<ParticipationRequest> participationRequestList = participationRequestRepository.findAllByEventId(eventId);

        log.info("Размер списка participationRequestList = {}", participationRequestList.size());

        if (event.getParticipantLimit() <= event.getConfirmedRequests()) {
            throw new ValidationException("Нельзя подтвердить заявку, уже достигнут лимит по заявкам на данное событие");
        }

        Long participantLimit = event.getParticipantLimit();
        log.info("Количество участников в событии participantLimit = {}", participantLimit);

        List<ParticipationRequest> allConfirmedRequests = participationRequestRepository.findAllByEventIdAndStatusIs(eventId, EventRequestStatus.CONFIRMED);
        log.info("Все подтвержденные заявки в событии allConfirmedRequests = {}", allConfirmedRequests.size());

        List<ParticipationRequest> allPendingRequests = participationRequestRepository.findAllByEventIdAndStatusIs(eventId, EventRequestStatus.PENDING);
        log.info("Все неподтвержденные заявки в событии allPendingRequests = {}", allPendingRequests.size());

        if (eventRequestStatusUpdateRequest.getStatus().equals("REJECTED")) {
            List<Long> confirmedIds = allConfirmedRequests
                    .stream()
                    .map(ParticipationRequest::getId)
                    .collect(Collectors.toList());
            List<Long> idsForUpdate1 = eventRequestStatusUpdateRequest.getRequestIds();

            List<Long> similarIds1 = confirmedIds
                    .stream()
                    .filter(idsForUpdate1::contains)
                    .collect(Collectors.toList());

            if (similarIds1.size() > 0) {
                throw new ValidationException("Нельзя отменить уже принятую заявку на участие.");
            }
            return toEventRequestStatusUpdateResult(allConfirmedRequests, allPendingRequests);
        }

        if (allConfirmedRequests.size() == participantLimit) {
            return toEventRequestStatusUpdateResult(allConfirmedRequests, allPendingRequests);
        }

        long countConfirmedRequests = event.getParticipantLimit() - allConfirmedRequests.size();

        List<ParticipationRequestDto> confirmedRequests = allPendingRequests
                .stream()
                .limit(countConfirmedRequests)
                .peek(r -> r.setStatus(EventRequestStatus.CONFIRMED))
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        log.info("Список peek confirmedRequests = {}", confirmedRequests);

        List<ParticipationRequest> allPendingRequests2 = participationRequestRepository.findAllByEventIdAndStatusIs(eventId, EventRequestStatus.PENDING);
        log.info("Количество всех неподтвержденных заявок в событии allPendingRequests2 = {}", allPendingRequests2.size());

        if (allPendingRequests2.size() == 0) {
            return ParticipationRequestMapper.toEventRequestStatusUpdateResult(confirmedRequests, Collections.emptyList());
        }

        List<ParticipationRequestDto> rejectedRequests = allPendingRequests2
                .stream()
                .peek(r -> r.setStatus(EventRequestStatus.REJECTED))
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        log.info("Список peek rejectedRequests = {}", rejectedRequests);

        return ParticipationRequestMapper.toEventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    @Override
    public List<ParticipationRequestDto> getOwnRequestsByRequester(long userId) {
        UserDto userDto = findUserInRepository(userId);

        List<ParticipationRequest> participationRequestList = participationRequestRepository.findAllByRequester(userDto);
        log.info("Найден participationRequestList = {}", participationRequestList);

        List<ParticipationRequestDto> allRequests = participationRequestList
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        log.info("Количество запросов на участие allRequests = {}", allRequests.size());
        return allRequests;
    }

    @SneakyThrows
    @Transactional
    @Override
    public ParticipationRequestDto createRequestByRequester(long userId, long eventId) {
        UserDto userDto = findUserInRepository(userId);
        Event event = findEventInRepository(eventId);

        if (participationRequestRepository.findByRequester(userDto).isPresent()) {
            throw new ValidationException("Обратите внимание: нельзя добавить повторный запрос.");
        }
        log.info("ЭТО НЕ ПОВТОРНЫЙ ЗАПРОС");
        if (event.getInitiator().equals(userDto)) {
            throw new ValidationException("Обратите внимание: инициатор события не может добавить запрос на участие в своём событии.");
        }
        log.info("ПОЛЬЗОВАТЕЛИ РАЗНЫЕ");
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Обратите внимание: нельзя участвовать в неопубликованном событии.");
        }
        log.info("СОБЫТИЕ ОПУБЛИКОВАНО");

        int confirmedRequests = participationRequestRepository.findAllByEventId(eventId).size();

        log.info("event.getConfirmedRequests() = {}", confirmedRequests);
        log.info("event.getParticipantLimit() = {}", event.getParticipantLimit());

        if (confirmedRequests >= event.getParticipantLimit()) {
            throw new ValidationException("Обратите внимание: у события достигнут лимит запросов на участие.");
        }

        log.info("ЛИМИТ УЧАСТНИКОВ НЕ ПРЕВЫШАЕТ ПОДТВЕРЖДЕННЫЕ ЗАЯВКИ");

        if (event.getRequestModeration()) {
            ParticipationRequest request = participationRequestRepository.save(ParticipationRequestMapper.toParticipationRequest(userDto, event, EventRequestStatus.PENDING));
            log.info("Запрос на участие в событии со статусом PENDING сохранен в базе = {}", request);

            return ParticipationRequestMapper.toParticipationRequestDto(request);
        }

        ParticipationRequest requestPublished = participationRequestRepository.save(ParticipationRequestMapper.toParticipationRequest(userDto, event, EventRequestStatus.CONFIRMED));
        log.info("Запрос на участие в событии со статусом CONFIRMED сохранен в базе = {}", requestPublished);

        return ParticipationRequestMapper.toParticipationRequestDto(requestPublished);
    }

    @SneakyThrows
    @Transactional
    @Override
    public ParticipationRequestDto cancelEventRequestByRequester(long userId, long requestId) {
        UserDto userDto = findUserInRepository(userId);
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Заявки на участие с таким id = %d нет в базе.", requestId)));

        if (!participationRequest.getRequester().equals(userDto)) {
            throw new ValidationException("Удалить запрос может только владелец запроса на участие.");
        }

        participationRequest.setStatus(EventRequestStatus.CANCELED);
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    private UserDto findUserInRepository(long userId) {
        UserDto userDto = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("Найден пользователь = {}", userDto);
        return userDto;
    }

    private CategoryDto findCategoryInRepository(long catId) {
        CategoryDto categoryDto = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категории с id = %d нет в базе", catId)));
        log.info("Найдена категория = {}", categoryDto);
        return categoryDto;
    }

    private Event findEventInRepository(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("События с id = %d в базе нет.", eventId)));
        log.info("Найдено событие, event = {}", event);
        return event;
    }

    private EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(
            List<ParticipationRequest> allConfirmedRequests, List<ParticipationRequest> allPendingRequests
    ) {
        List<ParticipationRequestDto> confirmedRequests = allConfirmedRequests
                .stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        log.info("Список confirmedRequests = {}", confirmedRequests);

        List<ParticipationRequestDto> rejectedRequests = allPendingRequests
                .stream()
                .peek(r -> r.setStatus(EventRequestStatus.REJECTED))
                .map(ParticipationRequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
        log.info("Список rejectedRequests = {}", rejectedRequests);

        return ParticipationRequestMapper.toEventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

}

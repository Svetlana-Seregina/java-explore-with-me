package ru.practicum.explorewithme.service.request;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.event.EventState;
import ru.practicum.explorewithme.dto.request.*;
import ru.practicum.explorewithme.dto.user.User;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.mappers.ParticipationRequestMapper;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final ParticipationRequestRepository participationRequestRepository;

    @SneakyThrows
    @Override
    public List<ParticipationRequestDto> findAllEventRequestsByInitiator(long userId, long eventId) {
        User user = findUserInRepository(userId);
        Event event = findEventInRepository(eventId);

        if (!event.getInitiator().equals(user)) {
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

        User user = findUserInRepository(userId);
        Event event = findEventInRepository(eventId);

        if (!event.getInitiator().equals(user)) {
            throw new ValidationException("Обновление запросов на участие может быть выполнено только организатором события.");
        }

        List<ParticipationRequest> participationRequestList = participationRequestRepository.findAllByEventIdAndStatusIs(eventId, EventRequestStatus.CONFIRMED);

        log.info("Размер списка participationRequestList = {}", participationRequestList.size());
        if (eventRequestStatusUpdateRequest.getStatus().equals("CONFIRMED")) {
            if (event.getParticipantLimit() <= participationRequestList.size()) {
                throw new ValidationException("Нельзя подтвердить заявку, уже достигнут лимит по заявкам на данное событие");
            }
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
        User user = findUserInRepository(userId);

        List<ParticipationRequest> participationRequestList = participationRequestRepository.findAllByRequester(user);
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
        User user = findUserInRepository(userId);
        Event event = findEventInRepository(eventId);

        if (participationRequestRepository.findByRequesterAndEvent(user, event).isPresent()) {
            throw new ValidationException("Обратите внимание: нельзя добавить повторный запрос.");
        }
        log.info("ЭТО НЕ ПОВТОРНЫЙ ЗАПРОС");
        if (event.getInitiator().equals(user)) {
            throw new ValidationException("Обратите внимание: инициатор события не может добавить запрос на участие в своём событии.");
        }
        log.info("ПОЛЬЗОВАТЕЛИ РАЗНЫЕ");
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Обратите внимание: нельзя участвовать в неопубликованном событии.");
        }
        log.info("СОБЫТИЕ ОПУБЛИКОВАНО");

        int confirmedRequests = participationRequestRepository.findAllByEventIdAndStatusIs(eventId, EventRequestStatus.CONFIRMED).size();

        log.info("confirmedRequests = {}", confirmedRequests);
        log.info("event.getParticipantLimit() = {}", event.getParticipantLimit());

        if (event.getParticipantLimit() != 0) {
            if (confirmedRequests == event.getParticipantLimit()) {
                throw new ValidationException("Обратите внимание: у события достигнут лимит запросов на участие.");
            }
        }

        log.info("ЛИМИТ УЧАСТНИКОВ НЕ ПРЕВЫШАЕТ ПОДТВЕРЖДЕННЫЕ ЗАЯВКИ");

        if (event.getRequestModeration()) {
            ParticipationRequest request = participationRequestRepository.save(ParticipationRequestMapper.toParticipationRequest(user, event, EventRequestStatus.PENDING));
            log.info("Запрос на участие в событии со статусом PENDING сохранен в базе = {}", request);

            return ParticipationRequestMapper.toParticipationRequestDto(request);
        }

        ParticipationRequest requestPublished = participationRequestRepository.save(ParticipationRequestMapper.toParticipationRequest(user, event, EventRequestStatus.CONFIRMED));
        log.info("Запрос на участие в событии со статусом CONFIRMED сохранен в базе = {}", requestPublished);

        return ParticipationRequestMapper.toParticipationRequestDto(requestPublished);
    }

    @SneakyThrows
    @Transactional
    @Override
    public ParticipationRequestDto cancelEventRequestByRequester(long userId, long requestId) {
        User user = findUserInRepository(userId);
        ParticipationRequest participationRequest = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Заявки на участие с таким id = %d нет в базе.", requestId)));

        if (!participationRequest.getRequester().equals(user)) {
            throw new ValidationException("Удалить запрос может только владелец запроса на участие.");
        }

        participationRequest.setStatus(EventRequestStatus.CANCELED);
        log.info("Заявка на участие отменена.");
        return ParticipationRequestMapper.toParticipationRequestDto(participationRequest);
    }

    private User findUserInRepository(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("Найден пользователь = {}", user);
        return user;
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

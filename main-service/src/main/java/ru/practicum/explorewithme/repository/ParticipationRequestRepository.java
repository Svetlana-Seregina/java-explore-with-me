package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.request.EventRequestStatus;
import ru.practicum.explorewithme.dto.request.ParticipationRequest;
import ru.practicum.explorewithme.dto.user.User;
import ru.practicum.explorewithme.dto.user.UserDto;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Optional<ParticipationRequest> findByRequesterAndEvent(User user, Event event);

    List<ParticipationRequest> findAllByEventId(long eventId);

    List<ParticipationRequest> findAllByRequester(User user);

    List<ParticipationRequest> findAllByEventIdAndStatusIs(long eventId, EventRequestStatus status);

    List<ParticipationRequest> findAllByEventIdInAndStatusIs(List<Long> eventIds, EventRequestStatus status);

    List<ParticipationRequest> findAllByEventId_In(List<Long> eventIds);
}

package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.dto.request.EventRequestStatus;
import ru.practicum.explorewithme.dto.request.ParticipationRequest;
import ru.practicum.explorewithme.dto.user.UserDto;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Optional<ParticipationRequest> findByRequester(UserDto userDto);

    List<ParticipationRequest> findAllByEventId(long eventId);

    List<ParticipationRequest> findAllByRequester(UserDto userDto);

    List<ParticipationRequest> findAllByEventIdAndStatusIs(long eventId, EventRequestStatus status);

    List<ParticipationRequest> findAllByEventIdInAndStatusIs(List<Long> eventIds, EventRequestStatus status);
}

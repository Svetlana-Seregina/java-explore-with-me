package ru.practicum.explorewithme.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.request.EventRequestStatus;
import ru.practicum.explorewithme.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.explorewithme.dto.request.ParticipationRequest;
import ru.practicum.explorewithme.dto.request.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.user.User;
import ru.practicum.explorewithme.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
public class ParticipationRequestMapper {

    public static ParticipationRequest toParticipationRequest(User user, Event event, EventRequestStatus state) {
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(user);
        participationRequest.setEvent(event);
        participationRequest.setCreated(LocalDateTime.now());
        participationRequest.setStatus(state);
        return participationRequest;
    }

    public static ParticipationRequestDto toParticipationRequestDto(ParticipationRequest participationRequest) {
        return new ParticipationRequestDto(
                participationRequest.getCreated(),
                participationRequest.getEvent().getId(),
                participationRequest.getId(),
                participationRequest.getRequester().getId(),
                participationRequest.getStatus().name()
        );
    }

    public static EventRequestStatusUpdateResult toEventRequestStatusUpdateResult(
            List<ParticipationRequestDto> confirmedRequests, List<ParticipationRequestDto> rejectedRequests) {
        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

}

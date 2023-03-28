package ru.practicum.explorewithme.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.Location;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public static Event toEvent(CategoryDto categoryDto, UserDto userDto, Location location, NewEventDto newEventDto, EventState eventState) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(categoryDto);
        event.setConfirmedRequests(0L);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setInitiator(userDto);
        event.setLocation(location);
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setState(eventState);
        event.setTitle(newEventDto.getTitle());
        return event;
    }

    public static EventFullDto toEventFullDto(Event event, Long confirmedRequests) {
        return new EventFullDto(
                event.getAnnotation(),
                event.getCategory(),
                confirmedRequests,
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                new EventFullDto.Location(event.getLocation().getLat(), event.getLocation().getLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(), // private Long confirmedRequests; // Количество одобренных заявок на участие в данном событии
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                new EventFullDto.Location(event.getLocation().getLat(), event.getLocation().getLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                event.getViews()// private Long views; // Количество просмотров события
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                event.getCategory(),
                event.getConfirmedRequests(), // confirmedRequests
                event.getEventDate(),
                event.getId(),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                event.getPaid(),
                event.getTitle(),
                event.getViews()
        );
    }

    public static EventShortDto toEventShortDtoWithConfirmedRequests(EventShortDto eventShortDto, Long confirmedRequests) {
        return new EventShortDto(
                eventShortDto.getAnnotation(),
                eventShortDto.getCategory(),
                confirmedRequests, // confirmedRequests
                eventShortDto.getEventDate(),
                eventShortDto.getId(),
                new UserShortDto(eventShortDto.getInitiator().getId(), eventShortDto.getInitiator().getName()),
                eventShortDto.getPaid(),
                eventShortDto.getTitle(),
                eventShortDto.getViews()
        );
    }
}

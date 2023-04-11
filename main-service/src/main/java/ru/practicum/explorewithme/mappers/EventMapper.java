package ru.practicum.explorewithme.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.Location;
import ru.practicum.explorewithme.dto.category.Category;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.event.*;
import ru.practicum.explorewithme.dto.user.User;
import ru.practicum.explorewithme.dto.user.UserShortDto;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public static Event toEvent(Category category, User user, Location location, NewEventDto newEventDto, EventState eventState) {
        Event event = new Event();
        event.setAnnotation(newEventDto.getAnnotation());
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setInitiator(user);
        event.setLocation(location);
        event.setPaid(newEventDto.isPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.isRequestModeration());
        event.setState(eventState);
        event.setTitle(newEventDto.getTitle());
        return event;
    }

    public static EventFullDto toEventFullDto(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                0L,
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
                0L
        );
    }

    public static EventFullDto toEventFullDto(Event event, Long confirmedRequests, Long views) {
        return new EventFullDto(
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
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
                views
        );
    }

    public static EventFullDto toEventFullDto(EventFullDto eventFullDto, Long confirmedRequests, Long views) {
        return new EventFullDto(
                eventFullDto.getAnnotation(),
                eventFullDto.getCategory(),
                confirmedRequests,
                eventFullDto.getCreatedOn(),
                eventFullDto.getDescription(),
                eventFullDto.getEventDate(),
                eventFullDto.getId(),
                new UserShortDto(eventFullDto.getInitiator().getId(), eventFullDto.getInitiator().getName()),
                new EventFullDto.Location(eventFullDto.getLocation().getLat(), eventFullDto.getLocation().getLon()),
                eventFullDto.getPaid(),
                eventFullDto.getParticipantLimit(),
                eventFullDto.getPublishedOn(),
                eventFullDto.getRequestModeration(),
                eventFullDto.getState(),
                eventFullDto.getTitle(),
                views
        );
    }

    public static EventFullDto toEventFullDtoWithViews(EventFullDto eventFullDto, Long views) {
        return new EventFullDto(
                eventFullDto.getAnnotation(),
                eventFullDto.getCategory(),
                eventFullDto.getConfirmedRequests(),
                eventFullDto.getCreatedOn(),
                eventFullDto.getDescription(),
                eventFullDto.getEventDate(),
                eventFullDto.getId(),
                new UserShortDto(eventFullDto.getInitiator().getId(), eventFullDto.getInitiator().getName()),
                new EventFullDto.Location(eventFullDto.getLocation().getLat(), eventFullDto.getLocation().getLon()),
                eventFullDto.getPaid(),
                eventFullDto.getParticipantLimit(),
                eventFullDto.getPublishedOn(),
                eventFullDto.getRequestModeration(),
                eventFullDto.getState(),
                eventFullDto.getTitle(),
                views
        );
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                new CategoryDto(event.getCategory().getId(), event.getCategory().getName()),
                0L,
                0L,
                event.getEventDate(),
                event.getId(),
                new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName()),
                event.getPaid(),
                event.getTitle(),
                0L
        );
    }

    public static EventShortDto toEventShortDtoWithConfirmedRequests(EventShortDto eventShortDto, Long confirmedRequests) {
        return new EventShortDto(
                eventShortDto.getAnnotation(),
                eventShortDto.getCategory(),
                confirmedRequests,
                eventShortDto.getComments(),
                eventShortDto.getEventDate(),
                eventShortDto.getId(),
                new UserShortDto(eventShortDto.getInitiator().getId(), eventShortDto.getInitiator().getName()),
                eventShortDto.getPaid(),
                eventShortDto.getTitle(),
                eventShortDto.getViews()
        );
    }

    public static EventShortDto toEventShortDtoWithViews(EventShortDto eventShortDto, Long views) {
        return new EventShortDto(
                eventShortDto.getAnnotation(),
                eventShortDto.getCategory(),
                eventShortDto.getConfirmedRequests(),
                eventShortDto.getComments(),
                eventShortDto.getEventDate(),
                eventShortDto.getId(),
                new UserShortDto(eventShortDto.getInitiator().getId(), eventShortDto.getInitiator().getName()),
                eventShortDto.getPaid(),
                eventShortDto.getTitle(),
                views
        );
    }


    public static EventShortDto toEventShortDtoWithComments(EventShortDto eventShortDto, Long allComments) {
        return new EventShortDto(
                eventShortDto.getAnnotation(),
                eventShortDto.getCategory(),
                eventShortDto.getConfirmedRequests(),
                allComments,
                eventShortDto.getEventDate(),
                eventShortDto.getId(),
                new UserShortDto(eventShortDto.getInitiator().getId(), eventShortDto.getInitiator().getName()),
                eventShortDto.getPaid(),
                eventShortDto.getTitle(),
                eventShortDto.getViews()
        );
    }
}

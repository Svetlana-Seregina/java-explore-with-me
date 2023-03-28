package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.event.EventState;
import ru.practicum.explorewithme.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiator(UserDto userDto, Pageable pageable);

    /*@Query(value = "SELECT " +
            "new ru.practicum.explorewithme.dto.event.EventShortDto " +
            "(e.annotation, e.category, e.confirmedRequests, e.eventDate, e.id, e.paid, e.title, e.views)" +
            "FROM Event AS e " +
            "WHERE e.initiator_id = ?1" *//*+
            "INNER JOIN Application AS a on ep.app.id = a.id " +
            "WHERE ep.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY a.name, ep.uri " +
            "ORDER BY COUNT(ep.uri) desc "*//*)
    List<EventShortDto> getAllByInitiator(long userDto);*/

    Event findByIdAndInitiator(long id, UserDto userDto);

    Page<Event> findAllByInitiatorInAndStateInAndEventDateIsAfterAndEventDateIsBefore
            (List<UserDto> users, List<EventState> states,
             LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    List<Event> findAllByCategory(CategoryDto categoryDto);

    List<Event> findAllByAnnotationIsContainingIgnoreCaseOrDescriptionIsContainingIgnoreCaseAndCategory_InAndEventDateIsAfterAndPaidAndStateIs(
            String text1, String text, List<CategoryDto> categoryDtoList, LocalDateTime start, Boolean paid, EventState state);

    List<Event> findAllByAnnotationIsContainingIgnoreCaseOrDescriptionIsContainingIgnoreCaseAndCategory_InAndEventDateIsAfterAndEventDateIsBeforeAndPaidAndStateIs(
            String text1, String text, List<CategoryDto> categoryDtoList, LocalDateTime start1, LocalDateTime start2, Boolean paid, EventState state);

}

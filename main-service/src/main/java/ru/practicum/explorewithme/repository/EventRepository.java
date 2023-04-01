package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.event.EventState;
import ru.practicum.explorewithme.dto.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiator(UserDto userDto, Pageable pageable);

    Event findByIdAndInitiator(long id, UserDto userDto);

    List<Event> findAllByCategory(CategoryDto categoryDto);

    @Query(value = "SELECT e FROM Event AS e " +
            "WHERE (COALESCE(:users) IS NULL OR e.initiator IN :users) " +
            "AND (COALESCE(:states) IS NULL OR e.state IN :states) " +
            "AND (COALESCE(:categories) IS NULL OR e.category IN :categories) " +
            "AND (DATE(:rangeStart) IS NULL OR e.eventDate > cast(:rangeStart as date)) " +
            "AND (DATE(:rangeEnd) IS NULL OR e.eventDate < cast(:rangeEnd as date))")
    Page<Event> findAllByQueryAdminParams(@Param("users") List<UserDto> users, @Param("states") List<EventState> states,
                                          @Param("categories") List<CategoryDto> categories,
                                          @Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd,
                                          Pageable pageable);

    @Query(value = "SELECT e FROM Event AS e " +
            "WHERE (:text IS NULL OR UPPER(e.annotation) LIKE CONCAT('%',UPPER(:text),'%')) " +
            "OR (:text IS NULL OR UPPER(e.description) LIKE CONCAT('%', UPPER(:text), '%')) " +
            "AND (COALESCE(:categories) IS NULL OR e.category IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (DATE(:rangeStart) IS NULL OR e.eventDate > cast(:rangeStart as date)) " +
            "AND (DATE(:rangeEnd) IS NULL OR e.eventDate < cast(:rangeEnd as date)) " +
            "AND (e.state = :state)")
    Page<Event> findAllByQueryPublicParams(@Param("text") String text, @Param("categories") List<CategoryDto> categories,
                                           @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                                           @Param("rangeEnd") LocalDateTime rangeEnd, @Param("state") EventState state,
                                           Pageable pageable);


}

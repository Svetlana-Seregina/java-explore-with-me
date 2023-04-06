package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.dto.category.Category;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.event.EventState;
import ru.practicum.explorewithme.dto.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    Page<Event> findAllByInitiator(User user, Pageable pageable);

    Event findByIdAndInitiator(long id, User user);

    List<Event> findAllByCategory(Category category);

    @Query(value = "SELECT e FROM Event AS e " +
            "WHERE (:users IS NULL OR e.initiator IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category IN :categories) " +
            "AND (DATE(:rangeStart) IS NULL OR e.eventDate > cast(:rangeStart as date)) " +
            "AND (DATE(:rangeEnd) IS NULL OR e.eventDate < cast(:rangeEnd as date))")
    Page<Event> findAllByQueryAdminParams(@Param("users") List<User> users, @Param("states") List<EventState> states,
                                          @Param("categories") List<Category> categories,
                                          @Param("rangeStart") LocalDateTime rangeStart, @Param("rangeEnd") LocalDateTime rangeEnd,
                                          Pageable pageable);

    @Query(value = "SELECT e FROM Event AS e " +
            "WHERE (:text IS NULL OR UPPER(e.annotation) LIKE CONCAT('%',UPPER(:text),'%')) " +
            "OR (:text IS NULL OR UPPER(e.description) LIKE CONCAT('%', UPPER(:text), '%')) " +
            "AND (:categories IS NULL OR e.category IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (DATE(:rangeStart) IS NULL OR e.eventDate > cast(:rangeStart as date)) " +
            "AND (DATE(:rangeEnd) IS NULL OR e.eventDate < cast(:rangeEnd as date)) " +
            "AND (e.state = :state)")
    Page<Event> findAllByQueryPublicParams(@Param("text") String text, @Param("categories") List<Category> categories,
                                           @Param("paid") Boolean paid, @Param("rangeStart") LocalDateTime rangeStart,
                                           @Param("rangeEnd") LocalDateTime rangeEnd, @Param("state") EventState state,
                                           Pageable pageable);

}

package ru.practicum.explorewithme;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT " +
            "distinct new ru.practicum.explorewithme.ViewStats(a.name, ep.uri, COUNT(ep.uri)) " +
            "FROM EndpointHit AS ep " +
            "INNER JOIN Application AS a on ep.app.id = a.id " +
            "WHERE ep.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY a.name, ep.uri " +
            "ORDER BY COUNT(ep.uri) desc ")
    List<ViewStats> findViewStatsWhenUrisIsEmptyAndIpIsUnique(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT " +
            "distinct new ru.practicum.explorewithme.ViewStats(a.name, ep.uri, COUNT(ep.uri)) " +
            "FROM EndpointHit AS ep " +
            "INNER JOIN Application AS a on ep.app.id = a.id " +
            "WHERE ep.timestamp BETWEEN ?1 AND ?2 " +
            "AND ep.uri IN ?3 " +
            "GROUP BY a.name, ep.uri " +
            "ORDER BY count(ep.uri) desc ")
    List<ViewStats> findViewStatsWhenUrisIsNotEmptyAndIpIsUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT " +
            "distinct new ru.practicum.explorewithme.ViewStats(a.name, ep.uri, COUNT(ep.uri)) " +
            "FROM EndpointHit AS ep " +
            "INNER JOIN Application AS a on ep.app.id = a.id " +
            "WHERE ep.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY a.name, ep.uri ")
    List<ViewStats> findViewStatsWhenUrisIsEventsAndIpIsUnique(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT " +
            "new ru.practicum.explorewithme.ViewStats(a.name, ep.uri, COUNT(ep.uri)) " +
            "FROM EndpointHit AS ep " +
            "INNER JOIN Application AS a on ep.app.id = a.id " +
            "WHERE ep.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY a.name, ep.uri " +
            "ORDER BY COUNT(ep.uri) desc ")
    List<ViewStats> findViewStatsWhenUrisIsEmptyAndIpIsNotUnique(LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT " +
            " new ru.practicum.explorewithme.ViewStats(a.name, ep.uri, COUNT(ep.uri)) " +
            "FROM EndpointHit AS ep " +
            "INNER JOIN Application AS a on ep.app.id = a.id " +
            "WHERE ep.timestamp BETWEEN ?1 AND ?2 " +
            "AND ep.uri IN ?3 " +
            "GROUP BY a.name, ep.uri " +
            "ORDER BY count(ep.uri) desc ")
    List<ViewStats> findViewStatsWhenUrisIsNotEmptyAndIpIsNotUnique(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query(value = "SELECT " +
            " new ru.practicum.explorewithme.ViewStats(a.name, ep.uri, COUNT(ep.uri)) " +
            "FROM EndpointHit AS ep " +
            "INNER JOIN Application AS a on ep.app.id = a.id " +
            "WHERE ep.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY a.name, ep.uri ")
        //+
    List<ViewStats> findViewStatsWhenUrisIsEventsAndIpIsNotUnique(LocalDateTime start, LocalDateTime end);

}
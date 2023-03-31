package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.StatsClient;
import ru.practicum.explorewithme.ViewStats;
import ru.practicum.explorewithme.dto.category.CategoryDto;
import ru.practicum.explorewithme.dto.compilation.Compilation;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.dto.event.EventState;
import ru.practicum.explorewithme.dto.request.EventRequestStatus;
import ru.practicum.explorewithme.dto.request.ParticipationRequest;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mappers.CompilationMapper;
import ru.practicum.explorewithme.mappers.EventMapper;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PublicServiceImpl implements PublicService {

    private final CategoryRepository categoryRepository;
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final StatsClient statsClient;
    private final ParticipationRequestRepository participationRequestRepository;

    @Override
    public List<CategoryDto> findAllCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<CategoryDto> categoryDtoList = categoryRepository.findAll(pageable)
                .stream().collect(Collectors.toList());
        if (categoryDtoList.size() == 0) {
            return Collections.emptyList();
        }
        log.info("Найдено {} категорий", categoryDtoList.size());
        return categoryDtoList;
    }

    @Override
    public CategoryDto findCategoryById(long catId) {
        CategoryDto category = categoryRepository.findById(catId)
                .orElseThrow(() -> new EntityNotFoundException("Категория не найдена в базе"));
        log.info("Найдена категория = {}", category);
        return category;
    }

    @Override
    public List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                             String sort, Integer from, Integer size,
                                             String path) {

        List<CategoryDto> categoryDtoList = null;
        if (categories != null) {
            categoryDtoList = categoryRepository.findAllById(categories);
            log.info("Найдена категория = {}", categoryDtoList);
        }

        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = new ArrayList<>();

        if (rangeStart != null) {
            events = eventRepository.findAllByQueryPublicParams(
                            text, categoryDtoList, paid, rangeStart, rangeEnd, EventState.PUBLISHED, pageable)
                    .stream()
                    .collect(toList());
            log.info("Найдены allEventsByQuery where rangeStart != null: {}", events);
        }
        if (rangeStart == null) {
            events = eventRepository.findAllByQueryPublicParams(
                            text, categoryDtoList, paid, LocalDateTime.now(), rangeEnd, EventState.PUBLISHED, pageable)
                    .stream()
                    .collect(toList());
            log.info("Найдены allEventsByQuery where rangeStart == null: {}", events);
        }

        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> eventIds = new ArrayList<>();
        for (Event event : events) {
            Long evId = event.getId();
            eventIds.add(evId);
        }

        Map<Long, List<ParticipationRequest>> confirmedRequestsByEventId =
                participationRequestRepository.findAllByEventIdInAndStatusIs(eventIds, EventRequestStatus.CONFIRMED)
                        .stream()
                        .collect(Collectors.groupingBy(b -> b.getEvent().getId(), toList()));
        log.info("confirmedRequestsByEventId = {}", confirmedRequestsByEventId.entrySet());

        LocalDateTime publishedDate = events
                .stream()
                .min(Comparator.comparing(Event::getPublishedOn))
                .map(Event::getPublishedOn)
                .orElseThrow();

        log.info("Самая ранняя дата в списке publishedDate = {}", publishedDate);

        LocalDateTime actualDate = LocalDateTime.now();
        log.info("Даты для поиска события: самая ранняя дата в списке publishedDate = {}; текущая дата actualDate = {}",
                publishedDate, actualDate);

        List<String> uris = new ArrayList<>();
        for (Event event : events) {
            uris.add("/events/" + event.getId());
        }
        Map<Long, List<ViewStats>> views = statsClient.getStats(publishedDate, actualDate, uris, false)
                .stream()
                .collect(Collectors.groupingBy(b -> (long) Integer.parseInt(b.getUri().substring(8)), toList()));

        log.info("Получена статистика views = {}", views.entrySet());

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(EventMapper::toEventShortDto)
                .map(eventShortDto -> {
                    if (confirmedRequestsByEventId.isEmpty()) {
                        return eventShortDto;
                    }
                    List<ParticipationRequest> participationRequests = confirmedRequestsByEventId.get(eventShortDto.getId());
                    int confirmedRequests = participationRequests.size();

                    return EventMapper.toEventShortDtoWithConfirmedRequests(eventShortDto, (long) confirmedRequests);
                })
                .map(eventShortDto -> {
                    List<ViewStats> viewStats = views.get(eventShortDto.getId());
                    if (viewStats != null) {
                        Long allViews = views.get(eventShortDto.getId())
                                .stream()
                                .map(ViewStats::getHits)
                                .findFirst()
                                .get();
                        return EventMapper.toEventShortDtoWithViews(eventShortDto, allViews);
                    }
                    return eventShortDto;
                })
                .collect(toList());
        log.info("eventShortDtoList = {}", eventShortDtoList);

        if (sort != null) {
            if (sort.equals("EVENT_DATE")) {
                return eventShortDtoList.stream()
                        .sorted(Comparator.comparing(EventShortDto::getEventDate))
                        .collect(toList());
            }
            if (sort.equals("VIEWS")) {
                return eventShortDtoList.stream()
                        .sorted(Comparator.comparing(EventShortDto::getViews))
                        .collect(toList());
            }
        }
        log.info("eventShortDtoList = {}", eventShortDtoList);
        return eventShortDtoList;
    }

    @Override
    public EventFullDto findEventById(long id, String path) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено в базе."));

        List<ParticipationRequest> participationRequests = participationRequestRepository.findAllByEventIdAndStatusIs(
                id, EventRequestStatus.CONFIRMED);

        int confirmedRequests = participationRequests.size();

        LocalDateTime publishedDate = event.getPublishedOn();
        LocalDateTime actualDate = LocalDateTime.now();
        log.info("Даты для поиска статистики о событии от publishedDate = {} до actualDate = {}", publishedDate, actualDate);

        List<String> uris = new ArrayList<>();
        uris.add(path);
        List<ViewStats> views = statsClient.getStats(publishedDate, actualDate, uris, false);
        log.info("Найдены просмотры события views = {}", views.size());
        Long hits = null;
        if (views.size() > 0) {
            hits = views.get(0).getHits();
        }
        if (views.size() == 0) {
            hits = 0L;
        }

        EventFullDto eventFullDto = EventMapper.toEventFullDto(event, (long) confirmedRequests, hits);
        log.info("Найдено событие по id = {}; EVENT = {}", id, eventFullDto);

        return eventFullDto;
    }

    @Override
    public List<CompilationDto> findAllCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Compilation> compilationList = compilationRepository.findAllByPinned(pinned, pageable)
                .stream().collect(Collectors.toList());
        log.info("РАЗМЕР СПИСКА compilationList = {}", compilationList.size());
        log.info("Полные данные подборки compilationList = {}", compilationList);


        List<EventShortDto> eventShortDtoList = compilationList.stream()
                .flatMap(ev -> ev.getEvents().stream()
                        .map(EventMapper::toEventShortDto))
                .collect(Collectors.toList());
        log.info("РАЗМЕР СПИСКА eventShortDtoList = {}", compilationList.size());

        List<CompilationDto> compilationDtoList = compilationList.stream()
                .map(c -> CompilationMapper.toCompilationDto(c, eventShortDtoList))
                .collect(Collectors.toList());
        log.info("РАЗМЕР СПИСКА compilationDtoList = {}", compilationDtoList.size());
        return compilationDtoList;
    }

    @Override
    public CompilationDto findCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException("Подборка не найдена в базе"));

        List<EventShortDto> eventShortDtoList = compilation.getEvents().stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());

        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
        log.info("Найдена подборка по id = {}, pinned = {}, title = {}, events.size = {}",
                compilationDto.getId(), compilationDto.getPinned(), compilationDto.getTitle(), compilationDto.getEvents().size());
        return compilationDto;
    }

}

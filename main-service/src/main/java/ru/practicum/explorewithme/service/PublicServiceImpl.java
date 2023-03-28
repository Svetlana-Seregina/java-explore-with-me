package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

import java.net.URISyntaxException;
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
    // информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
    // информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    // EventState.PUBLISHED,
    // text -> по annotation, description без учета регистра букв
    // ес. нет дат, то только события позже текущей даты и времени
    // должно быть кол-во views and confirmed requests
    // Вариант сортировки: по дате события или по количеству просмотров EVENT_DATE, VIEWS
    // save to stats-serv
    // вернуть пустой список, если по заданным пармаметрам ничего не найдено
    public List<EventShortDto> findAllEvents(String text, List<Long> categories, Boolean paid,
                                             LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable,
                                             String sort, Integer from, Integer size,
                                             String path) {

        List<CategoryDto> categoryDtoList = categoryRepository.findAllById(categories);
        log.info("Найден categoryDtoList.size = {} ", categoryDtoList.size());

        String textForRequest = text.toLowerCase();
        log.info("text для поиска = {}", textForRequest);

        if (rangeStart == null && rangeEnd == null) {
            List<Event> events = eventRepository.findAllByAnnotationIsContainingIgnoreCaseOrDescriptionIsContainingIgnoreCaseAndCategory_InAndEventDateIsAfterAndPaidAndStateIs(textForRequest, textForRequest, categoryDtoList, LocalDateTime.now(), paid, EventState.PUBLISHED);
            log.info("Найдены события events = {}", events.size());
            return toEventShortDto(events, path);
        }

        List<Event> events = eventRepository.findAllByAnnotationIsContainingIgnoreCaseOrDescriptionIsContainingIgnoreCaseAndCategory_InAndEventDateIsAfterAndEventDateIsBeforeAndPaidAndStateIs(
                textForRequest, textForRequest, categoryDtoList, rangeStart, rangeEnd, paid, EventState.PUBLISHED);
        log.info("Найдены события events = {}", events.size());

        return toEventShortDto(events, path);
    }

    @Override
    public EventFullDto findEventById(long id, String path) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Событие не найдено в базе."));
        List<ParticipationRequest> participationRequests = participationRequestRepository.findAllByEventIdAndStatusIs(id, EventRequestStatus.CONFIRMED);
        int confirmedRequests = participationRequests.size();
        EventFullDto eventFullDto = EventMapper.toEventFullDto(event, (long) confirmedRequests);
        log.info("Найдено событие по id = {}; EVENT = {}", id, eventFullDto);

        var publishedDate = event.getPublishedOn();
        var actualDate = LocalDateTime.now();
        log.info("Даты для поиска события по publishedDate = {}; actualDate = {}", publishedDate, actualDate);

        List<String> uris = new ArrayList<>();
        uris.add(path);
        ResponseEntity<Object> views = statsClient.getStats(publishedDate, actualDate, uris, false);
        log.info("Найдены просмотры события views = {}", views);

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

    @SneakyThrows
    private List<EventShortDto> toEventShortDto(List<Event> events, String path) {

        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        // если не пустой, то добавить подтвержденные заявки и просмотры
        // отсортировать по дате события или по количеству просмотров
        List<Long> eventIds = new ArrayList<>();
        for (Event event : events) {
            Long evId = event.getId();
            eventIds.add(evId);
        }

        Map<Long, List<ParticipationRequest>> confirmedRequestsByEventId =
                participationRequestRepository.findAllByEventIdInAndStatusIs(eventIds, EventRequestStatus.CONFIRMED)
                        .stream()
                        .collect(Collectors.groupingBy(b -> b.getEvent().getId(), toList()));
        log.info("confirmedRequestsByEventId = {}", confirmedRequestsByEventId.size());

        List<EventShortDto> eventShortDtos = events
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        log.info("Найдены события eventShortDtos = {} шт.", eventShortDtos.size());

        List<Long> uriIds = eventShortDtos
                .stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList());

        var publishedDate = events
                .stream()
                .min(Comparator.comparing(Event::getPublishedOn))
                .map(Event::getPublishedOn)
                .orElseThrow()
                ;

        log.info("Самая ранняя дата в списке publishedDate = {}", publishedDate);

        var actualDate = LocalDateTime.now();
        log.info("Даты для поиска события: самая ранняя дата в списке publishedDate = {}; текущая дата actualDate = {}",
                publishedDate, actualDate);

        List<String> uris = new ArrayList<>();
        for (Long id : uriIds) {
            String uri = path + "/" + id;
            uris.add(uri);
        }

        /*String urlToEncode = path;
            URIBuilder uriBuilder = new URIBuilder(urlToEncode);
            uriBuilder.addParameter("start", publishedDate.format())
            for(String uri : uris) {
                uriBuilder.addParameter("&uris=", uri);
            }
            System.out.println("Encoded Url: "+uriBuilder.toString());*/

        //ResponseEntity<Object> views = statsClient.getStats(publishedDate, actualDate, uris, false);

        //log.info("Получена статистика views = {}", views);

        /*Map<Long, List<ResponseEntity<Object>>> views = statsClient.getStats(publishedDate, actualDate, uris, false)
                .stream()
                .collect(Collectors.groupingBy(v -> v.get.getId(), toList()));
        log.info("views = {}", views.size());*/

        return events.stream()
                .map(EventMapper::toEventShortDto)
                .map(eventShortDto -> {
                    if (confirmedRequestsByEventId.isEmpty()) {
                        return eventShortDto;
                    }
                    List<ParticipationRequest> participationRequests = confirmedRequestsByEventId.get(eventShortDto.getId());
                    int confirmedRequests = participationRequests.size();
                    return EventMapper.toEventShortDtoWithConfirmedRequests(eventShortDto, (long) confirmedRequests);
                })
                /*.map(itemDtoBooking -> {
                    var allViews = views.get(itemDtoBooking.getId());
                    if (views != null) {
                        return ItemMapper.toItemDtoBookingWithComment(itemDtoBooking, views);
                    }
                    return itemDtoBooking;
                }) */
                .sorted(Comparator.comparing(EventShortDto::getEventDate))
                .collect(toList());

       //return eventShortDtos;

        // добавить подтвержденные запросы + просмотры
         /*switch (sort){
            case "EVENT_DATE":
                Sort sortByDate = Sort.by("eventDate").ascending();
                // code
                break;
            case "VIEWS":
                Sort sortByViews = Sort.by("views").ascending();
                // code
                break;
            default:
                List<Event> events = eventRepository.findAllByCategory_InAndPaidAndStateIs(categoryDtoList, paid, EventState.PUBLISHED);
                List<EventShortDto> eventShortDtos = events.stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList());
                log.info("Найдены события eventShortDtos = {} шт.", eventShortDtos.size());
                return eventShortDtos;
                // code
        }*/

    }


}

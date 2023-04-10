package ru.practicum.explorewithme.service.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.compilation.Compilation;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.event.EventShortDto;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mappers.CompilationMapper;
import ru.practicum.explorewithme.mappers.EventMapper;
import ru.practicum.explorewithme.repository.CompilationRepository;
import ru.practicum.explorewithme.repository.EventRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto createNewCompilation(NewCompilationDto newCompilationDto) {
        List<Long> eventIds = newCompilationDto.getEvents();
        if (eventIds == null || eventIds.isEmpty()) {
            Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, Collections.emptyList()));
            log.info("Создана подборка {}", compilation);
            CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation, Collections.emptyList());
            log.info("Количество событий в compilationDto: {}", compilationDto.getEvents().size());
            return compilationDto;
        }

        log.info("КОЛИЧЕСТВО --->> eventIds = {}", eventIds.size());
        List<Event> events = eventRepository.findAllById(eventIds);

        log.info("Количество events = {}", events.size());
        Compilation compilation = compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, events));
        log.info("Создана подборка {}", compilation);
        List<EventShortDto> eventShortDtoList = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        log.info("Количество eventShortDtoList = {}", eventShortDtoList.size());
        CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
        log.info("Количество событий в compilationDto: {}", compilationDto.getEvents().size());
        return compilationDto;
    }

    @Transactional
    @Override
    public boolean deleteCompilation(long compId) {
        compilationRepository.deleteById(compId);
        log.info("Подборка с id = {} удалена.", compId);
        boolean delete = compilationRepository.existsById(compId);
        log.info("Подборка есть в базе? = {}", delete);
        return delete;
    }

    @Transactional
    @Override
    public CompilationDto updateCompilationById(UpdateCompilationRequest updateCompilationRequest, long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Подборки с id = %d не найдено.", compId)));
        log.info("Найдена подборка в базе по id: {} до внесения изменений", compilation);

        List<Long> eventIds = updateCompilationRequest.getEvents();
        log.info("Количество eventIds: {}", eventIds);

        Set<Event> eventSet = new HashSet<>();

        compilation.setPinned(updateCompilationRequest.getPinned() != null ?
                updateCompilationRequest.getPinned() : compilation.getPinned());
        compilation.setTitle(updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank() ?
                updateCompilationRequest.getTitle() : compilation.getTitle());

        if (eventIds == null || eventIds.isEmpty()) {
            compilation.setEvents(eventSet);
            log.info("Создана подборка {}", compilation);
            CompilationDto compilationDto = CompilationMapper.toCompilationDto(compilation, Collections.emptyList());
            log.info("Количество событий в compilationDto: {}", compilationDto.getEvents().size());
            return compilationDto;
        }

        List<Event> events = eventRepository.findAllById(eventIds);
        log.info("Количество events: {}", events);

        eventSet = new HashSet<>(events);
        compilation.setEvents(eventSet);

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
        log.info("Количество eventShortDtoList: {}", eventShortDtoList);

        log.info("Подборка после внесения изменений: {}", compilation);

        return CompilationMapper.toCompilationDto(compilation, eventShortDtoList);
    }

    @Override
    public List<CompilationDto> findAllCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Compilation> compilationList = compilationRepository.findAllByPinned(pinned, pageable)
                .stream().collect(Collectors.toList());
        log.info("РАЗМЕР СПИСКА compilationList = {}", compilationList.size());
        log.info("Полные данные подборки compilationList = {}", compilationList);

        if (compilationList.isEmpty()) {
            return Collections.emptyList();
        }

        List<CompilationDto> compilationDtoList = compilationList.stream()
                .map(CompilationMapper::toCompilationDto)
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

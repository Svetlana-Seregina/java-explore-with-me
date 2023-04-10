package ru.practicum.explorewithme.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.service.compilation.CompilationService;

import java.util.List;

@RequestMapping("/compilations")
@RestController
@RequiredArgsConstructor
@Slf4j
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> findAllCompilations(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Обрабатываем запрос на поиск всех подборок закреплены(pinned)? = {}", pinned);
        return compilationService.findAllCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto findCompilationById(@PathVariable long compId) {
        log.info("Обрабатываем запрос на поиск подборки по id = {}", compId);
        return compilationService.findCompilationById(compId);
    }

}

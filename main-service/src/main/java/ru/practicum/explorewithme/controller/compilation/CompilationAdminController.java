package ru.practicum.explorewithme.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;
import ru.practicum.explorewithme.service.compilation.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> createNewCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("Обрабатываем запрос на создание подборки = {}", newCompilationDto);
        CompilationDto compilationDto = compilationService.createNewCompilation(newCompilationDto);
        return new ResponseEntity<>(compilationDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<Boolean> deleteCompilation(@PathVariable long compId) {
        log.info("Обрабатываем запрос на удаление подборки, id = {}", compId);
        boolean deleteCompilation = compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(deleteCompilation, HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilationById(@RequestBody UpdateCompilationRequest updateCompilationRequest,
                                                @PathVariable long compId) {
        log.info("Обрабатываем запрос на обновление подборки. Данные для обновления = {}", updateCompilationRequest);
        return compilationService.updateCompilationById(updateCompilationRequest, compId);
    }

}

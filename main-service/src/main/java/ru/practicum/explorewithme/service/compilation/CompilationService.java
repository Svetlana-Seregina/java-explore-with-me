package ru.practicum.explorewithme.service.compilation;

import ru.practicum.explorewithme.dto.compilation.CompilationDto;
import ru.practicum.explorewithme.dto.compilation.NewCompilationDto;
import ru.practicum.explorewithme.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createNewCompilation(NewCompilationDto newCompilationDto);

    boolean deleteCompilation(long compId);

    CompilationDto updateCompilationById(UpdateCompilationRequest updateCompilationRequest, long compId);

    List<CompilationDto> findAllCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto findCompilationById(long compId);

}

package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.dto.category.CategoryDto;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<CategoryDto, Long> {

    Optional<CategoryDto> findByName(String name);

}

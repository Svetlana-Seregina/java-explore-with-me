package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.dto.user.UserDto;

public interface UserRepository extends JpaRepository<UserDto, Long> {

}

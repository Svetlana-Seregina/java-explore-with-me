package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.dto.user.User;

public interface UserRepository extends JpaRepository<User, Long> {

}

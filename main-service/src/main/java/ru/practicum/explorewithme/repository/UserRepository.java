package ru.practicum.explorewithme.repository;

import com.sun.xml.bind.v2.model.core.ID;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.dto.user.UserDto;

import java.util.List;

public interface UserRepository extends JpaRepository<UserDto, Long> {

    List<UserDto> findAllByIdIn(List<Long> ids);

}

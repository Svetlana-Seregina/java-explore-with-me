package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.explorewithme.dto.user.UserDto;
import ru.practicum.explorewithme.dto.user.UserShortDto;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserDto, Long> {

    //List<UserDto> findAllById(Iterable<ID> ids, Pageable pageable);

    @Query(value = "SELECT " +
            "new ru.practicum.explorewithme.dto.user.UserShortDto(ud.id, ud.name) " +
            "FROM UserDto AS ud " +
            "WHERE ud.id = ?1 ")
    Optional<UserShortDto> findUserShortDtoById(long userId);

}

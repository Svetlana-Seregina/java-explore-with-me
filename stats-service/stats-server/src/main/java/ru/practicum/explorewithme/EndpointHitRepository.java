package ru.practicum.explorewithme;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.EndpointHit;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long>{
}

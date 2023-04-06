package ru.practicum.explorewithme.dto.request;

import lombok.*;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participation_request", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ParticipationRequest {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Идентификатор заявки

    private LocalDateTime created; // Дата и время создания заявки

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event; // Идентификатор события

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User requester; // Идентификатор пользователя, отправившего заявку

    @Enumerated(EnumType.STRING)
    private EventRequestStatus status; // Статус заявки

}

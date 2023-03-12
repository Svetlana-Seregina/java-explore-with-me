package ru.practicum.explorewithme.dto;

import lombok.*;
import ru.practicum.explorewithme.dto.event.EventFullDto;
import ru.practicum.explorewithme.dto.event.EventState;
import ru.practicum.explorewithme.dto.user.UserDto;

import javax.persistence.*;

@Entity
@Table(name = "participation_request", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Идентификатор заявки

    private String created; // Дата и время создания заявки

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private EventFullDto event; // Идентификатор события

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserDto requester; // Идентификатор пользователя, отправившего заявку

    private EventState status; // Статус заявки
}

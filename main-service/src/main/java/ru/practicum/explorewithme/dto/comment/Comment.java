package ru.practicum.explorewithme.dto.comment;

import lombok.*;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comment", schema = "public")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    private Event event;

    private String text;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

}

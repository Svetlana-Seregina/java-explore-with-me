package ru.practicum.explorewithme.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.explorewithme.dto.event.EventFullDto;

import javax.persistence.*;

@Entity
@Table(name = "compilation", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    // Подборка событий

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id")
    EventFullDto events;

    Boolean pinned; // Закреплена ли подборка на главной странице сайта

    String title; // Заголовок подборки

}

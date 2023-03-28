package ru.practicum.explorewithme.dto.compilation;

import lombok.*;
import ru.practicum.explorewithme.dto.event.Event;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "compilation", schema = "public")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "listOfEntities")
    private Set<Event> events = new HashSet<>();

    private Boolean pinned; // Закреплена ли подборка на главной странице сайта

    private String title; // Заголовок подборки

}

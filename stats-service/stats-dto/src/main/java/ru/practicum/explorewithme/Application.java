package ru.practicum.explorewithme;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "application", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}

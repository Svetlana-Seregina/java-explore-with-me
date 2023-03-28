package ru.practicum.explorewithme.dto.user;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDto {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;

}

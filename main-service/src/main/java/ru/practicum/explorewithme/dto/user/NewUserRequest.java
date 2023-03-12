package ru.practicum.explorewithme.dto.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class NewUserRequest {
    //Данные нового пользователя

    @NotBlank
    @Email
    private final String email;

    @NotBlank
    private final String name;
}

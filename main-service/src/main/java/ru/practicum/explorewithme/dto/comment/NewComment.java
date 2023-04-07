package ru.practicum.explorewithme.dto.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class NewComment {

    @NotBlank
    private String text;

}
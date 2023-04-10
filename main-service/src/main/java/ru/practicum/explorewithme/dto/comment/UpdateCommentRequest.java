package ru.practicum.explorewithme.dto.comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class UpdateCommentRequest {

    @NotBlank
    @Size(max = 7000)
    private String text;

}

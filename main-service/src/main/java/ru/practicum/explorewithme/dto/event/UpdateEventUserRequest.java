package ru.practicum.explorewithme.dto.event;

import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString
public class UpdateEventUserRequest extends UpdateEventRequest{

    private StateAction stateAction; // Изменение сотояния события: SEND_TO_REVIEW, CANCEL_REVIEW

    public enum StateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }

}

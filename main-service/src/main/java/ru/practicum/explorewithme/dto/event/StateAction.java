package ru.practicum.explorewithme.dto.event;

public enum StateAction { // Новое состояние события
    PUBLISH_EVENT, // UpdateEventAdminRequest
    REJECT_EVENT, // UpdateEventAdminRequest
    SEND_TO_REVIEW, // UpdateEventUserRequest
    CANCEL_REVIEW // UpdateEventUserRequest
}

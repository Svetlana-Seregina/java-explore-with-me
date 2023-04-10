package ru.practicum.explorewithme.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.dto.comment.Comment;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.NewComment;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.user.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public static Comment toComment(User user, Event event, NewComment newComment) {
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setText(newComment.getText());
        comment.setCreatedDate(LocalDateTime.now());
        return comment;
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getEvent().getId(),
                comment.getCreatedDate(),
                comment.getUpdatedDate()
        );
    }

}

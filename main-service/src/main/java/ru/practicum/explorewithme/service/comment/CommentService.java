package ru.practicum.explorewithme.service.comment;

import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.NewComment;

import java.util.List;

public interface CommentService {

    CommentDto addNewComment(long userId, long eventId, NewComment newComment);

    List<CommentDto> findAllCommentsByAuthor(long userId);

    List<CommentDto> findAllEventComments(long eventId);

}

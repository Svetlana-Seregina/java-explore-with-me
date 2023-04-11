package ru.practicum.explorewithme.service.comment;

import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.NewComment;
import ru.practicum.explorewithme.dto.comment.UpdateCommentRequest;

import java.util.List;

public interface CommentService {

    CommentDto addNewComment(long userId, long eventId, NewComment newComment);

    List<CommentDto> findAllCommentsByAuthor(long userId);

    List<CommentDto> findAllEventCommentsByUser(long eventId, Integer from, Integer size);

    boolean deleteCommentById(long commentId);

    CommentDto updateCommentByAuthor(long userId, long commentId, UpdateCommentRequest updateCommentRequest);

    boolean deleteEventCommentByAdmin(long commentId, long eventId);
}

package ru.practicum.explorewithme.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.NewComment;
import ru.practicum.explorewithme.service.comment.CommentService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class CommentPrivateController {

    private final CommentService commentService;

    @PostMapping("/{userId}/events/{eventId}/comments")
    public ResponseEntity<CommentDto> addNewComment(@PathVariable long userId,
                                                    @PathVariable long eventId,
                                                    @RequestBody @Valid NewComment newComment) {
        log.info("Обрабатываем запрос на добавление комментария: {}", newComment);
        CommentDto commentDto = commentService.addNewComment(userId, eventId, newComment);
        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @GetMapping("/{userId}/comments")
    public List<CommentDto> findAllCommentsByAuthor(@PathVariable long userId) {
        log.info("Обрабатываем запрос на поиск всех комментариев пользователя.");
        return commentService.findAllCommentsByAuthor(userId);
    }

}

package ru.practicum.explorewithme.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.service.comment.CommentService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class CommentAdminController {

    private final CommentService commentService;

    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    public ResponseEntity<Boolean> deleteEventCommentByAdmin(@PathVariable long commentId,
                                                          @PathVariable long eventId) {
        log.info("Обрабатываем запрос на удаление комментариия commentId = {} у события eventId = {}", commentId, eventId);
        boolean deleteComment = commentService.deleteEventCommentByAdmin(commentId, eventId);
        log.info("Комментарий удален ? = {}", deleteComment);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}

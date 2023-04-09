package ru.practicum.explorewithme.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.service.comment.CommentService;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/{eventId}/comments")
    public List<CommentDto> findAllCommentsByAuthor(@PathVariable long eventId) {
        log.info("Обрабатываем запрос на поиск всех комментариев к событию.");
        return commentService.findAllEventComments(eventId);
    }

}

package ru.practicum.explorewithme.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.service.comment.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentPublicController {

    private final CommentService commentService;

    @GetMapping("/events/{eventId}")
    public List<CommentDto> findAllEventCommentsByUser(@PathVariable long eventId,
                                                       @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                       @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Обрабатываем запрос на поиск всех комментариев к событию.");
        return commentService.findAllEventCommentsByUser(eventId, from, size);
    }

}

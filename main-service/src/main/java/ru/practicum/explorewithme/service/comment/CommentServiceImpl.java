package ru.practicum.explorewithme.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.comment.Comment;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.NewComment;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.user.User;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.mappers.CommentMapper;
import ru.practicum.explorewithme.repository.CommentRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @SneakyThrows
    @Transactional
    @Override
    public CommentDto addNewComment(long userId, long eventId, NewComment newComment) {
        User user = findUserInRepository(userId);
        Event event = findEventInRepository(eventId);

        Comment comment = commentRepository.save(CommentMapper.toComment(user, event, newComment));
        log.info("Создан новый комментарий: {}", comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> findAllCommentsByAuthor(long userId) {
        User user = findUserInRepository(userId);
        Sort sort = Sort.by("createdDate").descending();
        List<Comment> userComments = commentRepository.findAllByUser(user, sort);
        log.info("Найдено {} комментариев пользователя.", userComments.size());

        if(userComments.isEmpty()) {
            return Collections.emptyList();
        }

        return userComments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> findAllEventComments(long eventId) {
        Event event = findEventInRepository(eventId);

        List<Comment> allComments = commentRepository.findAllByEvent(event);
        List<CommentDto> allDtoComments = allComments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        log.info("Найдыны все комментарии к событию {}", allDtoComments);

        return allDtoComments;
    }


    private User findUserInRepository(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователя с id = %d нет в базе.", userId)));
        log.info("Найден пользователь = {}", user);
        return user;
    }

    private Event findEventInRepository(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("События с id = %d в базе нет.", eventId)));
        log.info("Найдено событие, event = {}", event);
        return event;
    }

}

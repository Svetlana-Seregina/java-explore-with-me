package ru.practicum.explorewithme.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explorewithme.dto.comment.Comment;
import ru.practicum.explorewithme.dto.comment.CommentDto;
import ru.practicum.explorewithme.dto.comment.NewComment;
import ru.practicum.explorewithme.dto.comment.UpdateCommentRequest;
import ru.practicum.explorewithme.dto.event.Event;
import ru.practicum.explorewithme.dto.event.EventState;
import ru.practicum.explorewithme.dto.user.User;
import ru.practicum.explorewithme.exception.EntityNotFoundException;
import ru.practicum.explorewithme.exception.ValidationException;
import ru.practicum.explorewithme.mappers.CommentMapper;
import ru.practicum.explorewithme.repository.CommentRepository;
import ru.practicum.explorewithme.repository.EventRepository;
import ru.practicum.explorewithme.repository.UserRepository;

import java.time.LocalDateTime;
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

        if (!event.getState().equals(EventState.PUBLISHED) && !event.getInitiator().equals(user)) {
            throw new ValidationException("Комментарии к неопубликованным событиям может оставлять только инициатор.");
        }

        Comment comment = commentRepository.save(CommentMapper.toComment(user, event, newComment));
        log.info("Создан новый комментарий: {}", comment);
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> findAllCommentsByAuthor(long userId) {
        User user = findUserInRepository(userId);
        Sort sort = Sort.by("createdDate").descending();
        List<Comment> userComments = commentRepository.findAllByAuthor(user, sort);
        log.info("Найдено {} комментариев пользователя.", userComments.size());

        if (userComments.isEmpty()) {
            return Collections.emptyList();
        }

        return userComments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> findAllEventCommentsByUser(long eventId, Integer from, Integer size) {
        Event event = findEventInRepository(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("Нельзя получить комментарии к неопубликованным событиям.");
        }

        Pageable pageable = PageRequest.of(from, size);
        List<Comment> allComments = commentRepository.findAllByEvent(event, pageable)
                .stream()
                .collect(Collectors.toList());
        log.info("найдены комментарии к событию = {}", allComments);

        List<CommentDto> allDtoComments = allComments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        log.info("Найдены все комментарии к событию {}", allDtoComments);

        return allDtoComments;
    }

    @Transactional
    @Override
    public boolean deleteCommentById(long commentId) {
        Comment comment = findCommentInRepository(commentId);

        if (comment.getCreatedDate().isAfter(LocalDateTime.now().plusMinutes(1))) {
            throw new ValidationException("Невозможно удалить комментарий оставленный более минуты назад.");
        }

        if (comment.getEvent().getState().equals(EventState.PENDING) && !comment.getAuthor().equals(comment.getEvent().getInitiator())) {
            throw new ValidationException("Невозможно удалить комментарий у неопубликованного события не инициатором.");
        }

        commentRepository.deleteById(commentId);
        log.info("Комментарий с id = {} удален.", commentId);

        return commentRepository.existsById(commentId);
    }

    @Transactional
    @Override
    public boolean deleteEventCommentByAdmin(long commentId, long eventId) {
        Comment comment = findCommentInRepository(commentId);
        Event event = findEventInRepository(eventId);

        if (!comment.getEvent().equals(event)) {
            throw new ValidationException("У события нет такого комментария.");
        }

        commentRepository.deleteById(commentId);
        log.info("Комментарий с id = {} удален.", commentId);

        return commentRepository.existsById(commentId);
    }

    @Transactional
    @Override
    public CommentDto updateCommentByAuthor(long userId, long commentId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = findCommentInRepository(commentId);
        User user = findUserInRepository(userId);

        if (!comment.getAuthor().equals(user)) {
            throw new ValidationException("Редактировать комментарий может только автор комментария.");
        }

        if (comment.getCreatedDate().isAfter(LocalDateTime.now().plusMinutes(1))) {
            throw new ValidationException("Невозможно редактировать комментарий оставленный более минуты назад.");
        }

        comment.setText(updateCommentRequest.getText());
        comment.setUpdatedDate(LocalDateTime.now());
        log.info("Комментарий с id = {} изменен.", commentId);

        return CommentMapper.toCommentDto(comment);
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

    private Comment findCommentInRepository(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("События с id = %d в базе нет.", commentId)));
        log.info("Найден комментарий, comment = {}", comment);
        return comment;
    }

}

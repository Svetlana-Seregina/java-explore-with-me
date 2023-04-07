package ru.practicum.explorewithme.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.dto.comment.Comment;
import ru.practicum.explorewithme.dto.user.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByUser(User user, Sort sort);
}
package ru.practicum.explorewithme.comments;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.comments.model.Comment;
import ru.practicum.explorewithme.comments.model.CommentStatus;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByEventId(long authorId);

    Optional<Comment> findById(long id);

    Optional<Comment> findByAuthorIdAndEventId(long userId, long eventId);

    List<Comment> findByAuthorIdAndStatus(long userId, CommentStatus status);
}

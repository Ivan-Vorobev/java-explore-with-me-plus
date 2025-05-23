package ru.practicum.explorewithme.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.comments.dto.CommentDto;
import ru.practicum.explorewithme.comments.dto.NewCommentDto;
import ru.practicum.explorewithme.comments.mapper.CommentMapper;
import ru.practicum.explorewithme.comments.model.Comment;
import ru.practicum.explorewithme.comments.model.CommentStatus;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.events.service.EventService;
import ru.practicum.explorewithme.exception.DataAlreadyExistException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.users.model.ParticipationRequest;
import ru.practicum.explorewithme.users.model.RequestStatus;
import ru.practicum.explorewithme.users.model.User;
import ru.practicum.explorewithme.users.repository.RequestRepository;
import ru.practicum.explorewithme.users.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explorewithme.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String COMMENT_NOT_FOUND_EXCEPTION_MESSAGE = "Комментарий с идентификатором {0} не найден!";
    private static final String COMMENT_ALREADY_EXISTS_EXCEPTION_MESSAGE =
            "Комментарий пользователя с идентификатором {0} к событию c идентификатором {1} уже существует!";

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;
    private final UserService userService;
    private final EventService eventService;

    @Override
    public List<CommentDto> findComments(long eventId) {

        eventService.findEventById(eventId);
        return commentMapper.toDto(commentRepository.findByEventId(eventId));
    }

    @Override
    public CommentDto findComment(long eventId, long commentId) {

        eventService.findEventById(eventId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(notFoundException(COMMENT_NOT_FOUND_EXCEPTION_MESSAGE, commentId));

        return commentMapper.toDto(comment);
    }

    @Override
    public CommentDto createComment(long userId, long eventId, NewCommentDto newCommentDto) {

        User user = userService.getUser(userId);
        Event event = eventService.findEventById(eventId);

        List<ParticipationRequest> participationRequests =
                requestRepository.findParticipationRequestByRequesterIdAndEventIdAndStatus(userId, eventId, RequestStatus.CONFIRMED);

        if (participationRequests.isEmpty()) {
            throw new NotFoundException("Нет запросов пользователя с ID: {0} к событию с ID: {1}", userId, eventId);
        }

        commentRepository.findByAuthorIdAndEventId(user.getId(), event.getId())
                .ifPresent(value -> {
                    throw new DataAlreadyExistException(COMMENT_ALREADY_EXISTS_EXCEPTION_MESSAGE, user.getId(), event.getId());
                });

        final LocalDateTime timestamp = LocalDateTime.now();

        final Comment newComment = Comment.builder()
                .text(newCommentDto.getText())
                .event(event)
                .author(user)
                .createdDate(timestamp)
                .updatedDate(timestamp)
                .publishedDate(timestamp)
                .status(CommentStatus.PENDING)
                .build();

        commentRepository.save(newComment);

        return commentMapper.toDto(newComment);
    }

    @Override
    public void deleteComment(long commentId) {

        commentRepository.findById(commentId)
                .orElseThrow(notFoundException(COMMENT_NOT_FOUND_EXCEPTION_MESSAGE, commentId));
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentDto patchCommentStatus(long commentId, CommentStatus status) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(notFoundException(COMMENT_NOT_FOUND_EXCEPTION_MESSAGE, commentId));

        comment.setStatus(status);
        commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }

    @Override
    public List<CommentDto> findApprovedCommentsOnUserId(long userId) {

        userService.getUser(userId);
        List<Comment> comments = commentRepository.findByAuthorIdAndStatus(userId, CommentStatus.APPROVED);

        return commentMapper.toDto(comments);
    }
}
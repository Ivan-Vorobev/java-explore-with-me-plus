package ru.practicum.explorewithme.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.comments.dto.CommentDto;
import ru.practicum.explorewithme.comments.dto.PrivateCommentParams;
import ru.practicum.explorewithme.comments.mapper.CommentMapper;
import ru.practicum.explorewithme.comments.model.Comment;
import ru.practicum.explorewithme.comments.model.CommentStatus;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.events.repository.EventRepository;
import ru.practicum.explorewithme.events.service.EventService;
import ru.practicum.explorewithme.exception.DataAlreadyExistException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.users.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.users.model.RequestStatus;
import ru.practicum.explorewithme.users.model.User;
import ru.practicum.explorewithme.users.service.RequestService;
import ru.practicum.explorewithme.users.service.UserService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static ru.practicum.explorewithme.exception.ForbiddenException.forbiddenException;
import static ru.practicum.explorewithme.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String EVENT_NOT_FOUND_EXCEPTION_MESSAGE = "Событие с идентификатором {0} не найдено!";
    private static final String COMMENT_NOT_FOUND_EXCEPTION_MESSAGE = "Комментарий с идентификатором {0} не найден!";

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final RequestService requestService;
    private final EventService eventService;

    @Override
    public List<CommentDto> findComments(long eventId) {

        checkExistEvent(eventId);
        return commentMapper.toDto(commentRepository.findByEventId(eventId));
    }

    @Override
    public CommentDto findComment(long eventId, long commentId) {

        checkExistEvent(eventId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(notFoundException(COMMENT_NOT_FOUND_EXCEPTION_MESSAGE, eventId));

        return commentMapper.toDto(comment);
    }

    @Override
    public CommentDto createComment(PrivateCommentParams params) {
        final User user = userService.getUser(params.getUserId());
        final Event event = eventService.findEventById(params.getEventId());
        final Collection<ParticipationRequestDto> currentRequests = requestService
                .findUserRequestsOnEvent(params.getUserId(), params.getEventId());
        if (currentRequests.isEmpty()) {
            final String error = String.format("Not found any requests from user id=%d on event id=%d.",
                    user.getId(), event.getId());
            throw new NotFoundException(error);
        }
        final Optional<Comment> currentComment = commentRepository.findByAuthorIdAndEventId(user.getId(), event.getId());
        if (currentComment.isPresent()) {
            final String error = String.format("Comment from user id=%d to event id=%d already exists.",
                    user.getId(), event.getId());
            throw new DataAlreadyExistException(error);
        }
        currentRequests.stream()
                .filter(request -> request.getStatus().equals(RequestStatus.CONFIRMED))
                .findFirst()
                .orElseThrow(forbiddenException("Events can only be commented on if requests to participate in them " +
                                                "are confirmed."));
        final LocalDateTime now = LocalDateTime.now();
        final Comment newComment = Comment.builder()
                .text(params.getNewCommentDto().getText())
                .event(event)
                .author(user)
                .createdDate(now)
                .updatedDate(now)
                .publishedDate(now)
                .status(CommentStatus.PENDING)
                .build();
        return commentMapper.toDto(commentRepository.save(newComment));
    }

    private void checkExistEvent(long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(notFoundException(EVENT_NOT_FOUND_EXCEPTION_MESSAGE, eventId));
    }
}

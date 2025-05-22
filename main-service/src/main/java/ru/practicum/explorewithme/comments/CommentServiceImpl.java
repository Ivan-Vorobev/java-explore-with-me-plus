package ru.practicum.explorewithme.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.comments.dto.CommentDto;
import ru.practicum.explorewithme.comments.mapper.CommentMapper;
import ru.practicum.explorewithme.comments.model.Comment;
import ru.practicum.explorewithme.events.repository.EventRepository;

import java.util.List;

import static ru.practicum.explorewithme.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private static final String EVENT_NOT_FOUND_EXCEPTION_MESSAGE = "Событие с идентификатором {0} не найдено!";
    private static final String COMMENT_NOT_FOUND_EXCEPTION_MESSAGE = "Комментарий с идентификатором {0} не найден!";

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

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

    private void checkExistEvent(long eventId) {
        eventRepository.findById(eventId)
                .orElseThrow(notFoundException(EVENT_NOT_FOUND_EXCEPTION_MESSAGE, eventId));
    }
}

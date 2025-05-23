package ru.practicum.explorewithme.comments;

import ru.practicum.explorewithme.comments.dto.CommentDto;
import ru.practicum.explorewithme.comments.dto.NewCommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> findComments(long eventId);

    CommentDto findComment(long eventId, long commentId);

    CommentDto createComment(long userId, long eventId, NewCommentDto newCommentDto);
}

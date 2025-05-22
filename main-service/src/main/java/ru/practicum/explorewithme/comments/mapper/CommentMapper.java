package ru.practicum.explorewithme.comments.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.comments.dto.CommentDto;
import ru.practicum.explorewithme.comments.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    CommentDto toDto(Comment comment);

    Comment toModel(CommentDto commentDto);
}
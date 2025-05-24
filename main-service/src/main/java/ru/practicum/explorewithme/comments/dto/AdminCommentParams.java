package ru.practicum.explorewithme.comments.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.comments.model.CommentStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class AdminCommentParams {
    private List<Long> comments;
    private String text;
    private List<Long> events;
    private List<Long> authors;
    private List<CommentStatus> status;
    private LocalDateTime createdDateStart;
    private LocalDateTime createdDateEnd;
    private LocalDateTime publishedDateStart;
    private LocalDateTime publishedDateEnd;
    private Integer from;
    private Integer size;
}

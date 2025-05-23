package ru.practicum.explorewithme.comments.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrivateCommentParams {
    private Long userId;
    private Long eventId;
    private NewCommentDto newCommentDto;
}

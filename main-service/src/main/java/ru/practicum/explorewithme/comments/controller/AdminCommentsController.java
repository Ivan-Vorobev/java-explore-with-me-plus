package ru.practicum.explorewithme.comments.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.comments.CommentService;
import ru.practicum.explorewithme.comments.dto.AdminCommentParams;
import ru.practicum.explorewithme.comments.dto.CommentDto;
import ru.practicum.explorewithme.comments.dto.CommentPatchDto;
import ru.practicum.explorewithme.comments.model.CommentStatus;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentsController {

    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> findAllComments(
            @RequestParam(required = false) List<Long> comments,
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) List<Long> authors,
            @RequestParam(required = false) List<CommentStatus> status,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdDateStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdDateEnd,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime publishedDateStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime publishedDateEnd,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "to", defaultValue = "10") Integer size
    ) {
        AdminCommentParams adminCommentParams = AdminCommentParams
                .builder()
                .comments(comments)
                .text(text)
                .events(events)
                .authors(authors)
                .status(status)
                .createdDateStart(createdDateStart)
                .createdDateEnd(createdDateEnd)
                .publishedDateStart(publishedDateStart)
                .publishedDateEnd(publishedDateEnd)
                .from(from)
                .size(size)
                .build();

        return commentService.findAllByAdminParams(adminCommentParams);
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto findCommentById(@PathVariable long commentId) {
        return commentService.findCommentById(commentId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long commentId) {
        commentService.deleteComment(commentId);
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto patchCommentStatus(@PathVariable long commentId,
                                         @RequestBody CommentPatchDto commentPatchDto) {

        CommentStatus status = commentPatchDto.getStatus();
        return commentService.patchCommentStatus(commentId, status);
    }
}
package ru.practicum.explorewithme.users.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.users.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.users.service.RequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateUserController {

    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findAllRequests(@PathVariable long userId) {
        log.info("Выполняется получение всех запросов пользователя: {}", userId);
        return requestService.findAllRequestsByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto save(@PathVariable long userId,
                                        @RequestParam("eventId") @NotNull long eventId) {
        log.info("Выполняется добавление запроса от пользователя {} на участие в событии: {}", userId, eventId);
        return requestService.save(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{request-id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable long userId,
                                                 @PathVariable("request-id") long requestId) {
        log.info("Выполняется отмена запроса пользователя {} на участие в событии: {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
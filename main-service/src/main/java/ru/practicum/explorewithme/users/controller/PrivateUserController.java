package ru.practicum.explorewithme.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.users.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.users.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateUserController {

    private static final String USER_ID_PATH = "/{user-id}";
    private static final String REQUESTS_PATH = "/requests";

    private final RequestService requestService;

    @GetMapping(USER_ID_PATH + REQUESTS_PATH)
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findAllRequests(@PathVariable("user-id") long userId) {
        log.info("Выполняется получение всех запросов пользователя: {}", userId);
        return requestService.findAllRequestsByUserId(userId);
    }

    @PostMapping(USER_ID_PATH + REQUESTS_PATH)
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto save(@PathVariable("user-id") long userId,
                                        @RequestParam("eventId") long eventId) {
        log.info("Выполняется добавление запроса от пользователя {} на участие в событии: {}", userId, eventId);
        return requestService.save(userId, eventId);
    }

    @PatchMapping(USER_ID_PATH + REQUESTS_PATH + "/{request-id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable("user-id") long userId,
                                                 @PathVariable("request-id") long requestId) {
        log.info("Выполняется отмена запроса пользователя {} на участие в событии: {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }
}
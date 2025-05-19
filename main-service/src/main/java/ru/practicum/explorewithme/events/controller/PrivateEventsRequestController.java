package ru.practicum.explorewithme.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.users.dto.ChangeRequestStatusDto;
import ru.practicum.explorewithme.users.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.users.dto.UserParticipationRequestDto;
import ru.practicum.explorewithme.users.service.RequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/events/{eventId}/requests")
@RequiredArgsConstructor
public class PrivateEventsRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> findUserRequestsOnEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.findUserRequestsOnEvent(userId, eventId);
    }

    @PatchMapping
    public UserParticipationRequestDto patchRequestStatus(@RequestBody ChangeRequestStatusDto changeRequestStatusDto,
                                                          @PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.patchRequestStatus(changeRequestStatusDto, userId, eventId);
    }
}

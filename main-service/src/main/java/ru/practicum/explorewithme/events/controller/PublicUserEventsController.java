package ru.practicum.explorewithme.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.NewEventDto;
import ru.practicum.explorewithme.events.service.EventService;
import ru.practicum.explorewithme.events.dto.RequestMethod;

import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}")
@RequiredArgsConstructor
@Slf4j
public class PublicUserEventsController {
    private final EventService eventService;

    @GetMapping("/events")
    public List<EventDto> getEvents(
            @Valid @NotNull @PathVariable Long userId,
            @Valid @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Valid @RequestParam(value = "to", defaultValue = "10") Integer to
    ) {

        return eventService.findAllByParams(userId, from, to);
    }

    @PostMapping("/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(
            @Valid @NotNull @PathVariable Long userId,
            @Validated({RequestMethod.Create.class}) @RequestBody NewEventDto eventDto
    ) {
        return eventService.addEvent(eventDto, userId);
    }

    @GetMapping("/events/{eventId}")
    public EventDto getUserEvent(
            @Valid @NotNull @PathVariable Long userId,
            @Valid @NotNull @PathVariable Long eventId
    ) {

        return eventService.findUserEvent(userId, eventId);
    }

    @PatchMapping("/events/{eventId}")
    public EventDto updateUserEvent(
            @Valid @NotNull @PathVariable Long userId,
            @Valid @NotNull @PathVariable Long eventId,
            @Validated({RequestMethod.Update.class}) @RequestBody NewEventDto eventDto
    ) {

        return eventService.updateEventByUser(eventId, eventDto, userId);
    }
}

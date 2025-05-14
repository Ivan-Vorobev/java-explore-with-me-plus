package ru.practicum.explorewithme.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.NewEventDto;

@RestController
@RequestMapping(path = "/users/{userId}")
public class PublicEventsController {
    @PostMapping("/events")
    public EventDto createEvent(
            @Valid @NotNull @PathVariable Long userId,
            @RequestBody @Valid NewEventDto eventDto
    ) {
        return null;
    }
}

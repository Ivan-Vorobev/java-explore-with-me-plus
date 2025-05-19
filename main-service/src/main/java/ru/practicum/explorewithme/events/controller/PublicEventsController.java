package ru.practicum.explorewithme.events.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
@Slf4j
public class PublicEventsController {
    private final EventService eventService;

    @GetMapping
    public List<EventDto> getEvents(
            HttpServletRequest request,
            @RequestParam String text,
            @RequestParam List<Long> categories,
            @RequestParam Boolean paid,
            @RequestParam Boolean onlyAvailable,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam UserEventParams.EventSortEnum sort,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "to", defaultValue = "10") Integer size
    ) {
        UserEventParams userEventParams = UserEventParams
                .builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        eventService.sendHit(request);
        return eventService.findAllByUserParams(userEventParams);
    }

    @GetMapping("/{eventId}")
    public EventDto getPublishedEvent(
            HttpServletRequest request,
            @NotNull @PathVariable Long eventId
    ) {
        eventService.sendHit(request);
        return eventService.findPublishedEvent(eventId);
    }

//
//    @PostMapping("/events")
//    public EventDto createEvent(
//            @Valid @NotNull @PathVariable Long userId,
//            @Valid @RequestBody NewEventDto eventDto
//    ) {
//        return eventService.addEvent(eventDto, userId);
//    }
//
//    @GetMapping("/events/{eventId}")
//    public EventDto getUserEvent(
//            @Valid @NotNull @PathVariable Long userId,
//            @Valid @NotNull @PathVariable Long eventId
//    ) {
//
//        return eventService.findUserEvent(userId, eventId);
//    }
//
//    @PatchMapping("/events/{eventId}")
//    public EventDto updateUserEvent(
//            @Valid @NotNull @PathVariable Long userId,
//            @Valid @RequestBody NewEventDto eventDto,
//            @Valid @NotNull @PathVariable Long eventId
//    ) {
//
//        return eventService.updateEventByUser(eventId, eventDto, userId);
//    }
}

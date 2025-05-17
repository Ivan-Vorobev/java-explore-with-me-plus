package ru.practicum.explorewithme.events.service;

import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.NewEventDto;

import java.util.List;

public interface EventService {
    EventDto addEvent(NewEventDto newEventDto, Long userId);

    List<EventDto> findAllByParams(Long userId, Integer from, Integer to);

    EventDto findUserEvent(Long userId, Long eventId);
}

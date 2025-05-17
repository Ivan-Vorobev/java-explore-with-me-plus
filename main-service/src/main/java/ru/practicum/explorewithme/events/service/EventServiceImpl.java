package ru.practicum.explorewithme.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.categories.dto.CategoryDto;
import ru.practicum.explorewithme.categories.service.CategoryService;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.NewEventDto;
import ru.practicum.explorewithme.events.enumiration.EventState;
import ru.practicum.explorewithme.events.mapper.EventMapper;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.events.repository.EventRepository;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.users.dto.ShortUserDto;
import ru.practicum.explorewithme.users.dto.UserDto;
import ru.practicum.explorewithme.users.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    @Override
    public EventDto addEvent(NewEventDto newEventDto, Long userId) {
        UserDto userDto = userService.getById(userId);
        CategoryDto categoryDto = categoryService.getCategory(newEventDto.getCategory());

        EventDto eventDto = eventMapper.convertShortDto(newEventDto);
        Event event = eventMapper.toModel(eventDto);

        Event savedEvent = eventRepository.save(event);
        EventDto savedEventDto = eventMapper.toDto(savedEvent);
        savedEventDto.setCategory(CategoryDto
                .builder()
                .id(categoryDto.getId())
                .name(categoryDto.getName())
                .build()
        );
        savedEventDto.setInitiator(ShortUserDto
                .builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .build()
        );

        return savedEventDto;
    }

    @Override
    public EventDto updateEvent(Long eventId, NewEventDto newEventDto, Long userId) {
        Event event = findEventById(eventId);
        checkInitiatorId(event, userId);

        if (List.of(EventState.CANCELED, EventState.PENDING).contains(event.getState())) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }


        return null;
    }

    @Override
    public List<EventDto> findAllByParams(Long userId, Integer from, Integer size) {
        UserDto userDto = userService.getById(userId);
        int page = from / size + 1;
        List<Event> events = eventRepository.findAllByInitiatorId(userDto.getId(), PageRequest.of(page, size)).getContent();

        return eventMapper.toDto(events);
    }

    @Override
    public EventDto findUserEvent(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        checkInitiatorId(event, userId);

        return eventMapper.toDto(event);
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    private void checkInitiatorId(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event with id " + event.getId() + "don't compare with initiator " + userId);
        }
    }

}

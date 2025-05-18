package ru.practicum.explorewithme.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.client.stats.StatsClient;
import ru.practicum.dto.HitsStatDTO;
import ru.practicum.explorewithme.categories.dto.CategoryDto;
import ru.practicum.explorewithme.categories.service.CategoryService;
import ru.practicum.explorewithme.events.controller.AdminEventParams;
import ru.practicum.explorewithme.events.controller.UserEventParams;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.NewEventDto;
import ru.practicum.explorewithme.events.enumiration.EventState;
import ru.practicum.explorewithme.events.enumiration.EventStateAction;
import ru.practicum.explorewithme.events.mapper.EventMapper;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.events.repository.EventRepository;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.users.dto.ShortUserDto;
import ru.practicum.explorewithme.users.dto.UserDto;
import ru.practicum.explorewithme.users.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.explorewithme.events.repository.EventRepository.AdminEventSpecification.withAdminEventParams;
import static ru.practicum.explorewithme.events.repository.EventRepository.UserEventSpecification.withUserEventParams;

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

        savedEventDto.setViews(0L);
        savedEventDto.setConfirmedRequests(0);

        return savedEventDto;
    }

    @Override
    public EventDto updateEventByUser(Long eventId, NewEventDto newEventDto, Long userId) {
        Event event = findEventById(eventId);
        checkInitiatorId(event, userId);

        if (List.of(EventState.CANCELED, EventState.PENDING).contains(event.getState())) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        return doUpdateEvent(event, newEventDto);
    }

    @Override
    public EventDto updateEventByAdmin(Long eventId, NewEventDto newEventDto) {
        Event event = findEventById(eventId);

        if (!Objects.isNull(newEventDto.getStateAction())) {
            if (List.of(EventStateAction.PUBLISH_EVENT, EventStateAction.REJECT_EVENT).contains(newEventDto.getStateAction())) {
                throw new ConflictException("Invalid action: " + newEventDto.getStateAction());
            }

            if (newEventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT) && !event.getState().equals(EventState.PENDING)) {
                throw new ConflictException("Cannot change state the event because it's not in the right state: PENDING");
            }

            if (newEventDto.getStateAction().equals(EventStateAction.REJECT_EVENT) && event.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictException("Cannot change state the event because it's not in the right state: PUBLISHED");
            }

            if (newEventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                LocalDateTime checkPublishDate = LocalDateTime.now().plusHours(1L);
                if (
                        (!Objects.isNull(newEventDto.getEventDate()) && checkPublishDate.isBefore(newEventDto.getEventDate()))
                                || checkPublishDate.isBefore(event.getEventDate())
                ) {
                    throw new ConflictException("The start date of the modified event must be no earlier than one hour from the publication date");
                }
            }
        }

        return doUpdateEvent(event, newEventDto);
    }

    @Override
    public List<EventDto> findAllByParams(Long userId, Integer from, Integer size) {
        UserDto userDto = userService.getById(userId);
        int page = from / size + 1;
        List<Event> events = eventRepository.findAllByInitiatorId(userDto.getId(), PageRequest.of(page, size)).getContent();

        List<EventDto> eventDtos = eventMapper.toDto(events);

        loadViews(
                eventDtos,
                eventDtos.stream().map(EventDto::getCreatedOn).min(LocalDateTime::compareTo).orElse(LocalDateTime.now()),
                eventDtos.stream().map(EventDto::getEventDate).min(LocalDateTime::compareTo).orElse(null)
        );
        loadConfirmedRequests(eventDtos);

        return eventDtos;
    }

    @Override
    public EventDto findUserEvent(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        checkInitiatorId(event, userId);

        EventDto eventDto = eventMapper.toDto(event);

        loadViews(List.of(eventDto), event.getCreatedOn(), event.getEventDate());
        loadConfirmedRequests(List.of(eventDto));

        return eventDto;
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public List<EventDto> findAllByAdminParams(AdminEventParams adminEventParams) {
        PageRequest pageRequest = PageRequest.of(
                adminEventParams.getFrom() / adminEventParams.getSize(),
                adminEventParams.getSize(),
                Sort.by("eventDate").ascending()
        );

        Page<Event> events = eventRepository.findAll(withAdminEventParams(adminEventParams), pageRequest);

        List<EventDto> eventDtos = eventMapper.toDto(events.stream().toList());
        loadViews(eventDtos, adminEventParams.getRangeStart(), adminEventParams.getRangeEnd());
        loadConfirmedRequests(eventDtos);

        return eventDtos;
    }

    @Override
    public List<EventDto> findAllByAdminParams(UserEventParams userEventParams) {
        PageRequest pageRequest = PageRequest.of(
                userEventParams.getFrom() / userEventParams.getSize(),
                userEventParams.getSize(),
                Sort.by("eventDate").ascending()
        );

        Page<Event> events = eventRepository.findAll(withUserEventParams(userEventParams), pageRequest);

        List<EventDto> eventDtos = eventMapper.toDto(events.stream().toList());
        loadViews(eventDtos, userEventParams.getRangeStart(), userEventParams.getRangeEnd());
        loadConfirmedRequests(eventDtos);

        return eventDtos;
    }

    private void checkInitiatorId(Event event, Long userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Event with id " + event.getId() + "don't compare with initiator " + userId);
        }
    }

    private void loadConfirmedRequests(List<EventDto> events) {
        List<Long> eventIds = events
                .stream()
                .map(EventDto::getId)
                .toList();

        // TODO:: перевести реквесты
        Map<Long, Integer> requests = null;

        events.forEach(event -> {
            Integer requestCount = 0;
            if (!Objects.isNull(requests)) {
                requestCount = requests.get(event.getId());
                requestCount = Objects.isNull(requestCount) ? 0 : requestCount;
            }
            event.setConfirmedRequests(requestCount);
        });
    }

    private void loadViews(List<EventDto> events, LocalDateTime start, LocalDateTime end) {
        Map<Long, String> eventIds = events
                .stream()
                .collect(Collectors.toMap(
                        EventDto::getId,
                        event -> "/events/" + event.getId()
                ));

        Optional<Collection<HitsStatDTO>> hitsStatDTOS = new StatsClient().getAll(
                start,
                end,
                eventIds.values().stream().toList(),
                false
        );

        if (hitsStatDTOS.isPresent() && !hitsStatDTOS.get().isEmpty()) {
            Map<String, Long> hitsStats = hitsStatDTOS.get()
                    .stream()
                    .collect(Collectors.toMap(HitsStatDTO::getUri, HitsStatDTO::getHits));

            events.forEach(event -> {
                Long hit = hitsStats.get(eventIds.get(event.getId()));
                event.setViews(Objects.isNull(hit) ? 0L : hit);
            });

        } else {
            events.forEach(event -> event.setViews(0L));
        }
    }

    private EventDto doUpdateEvent(Event currentEvent, NewEventDto newEventDto) {
        if (!Objects.isNull(newEventDto.getAnnotation())) {
            currentEvent.setAnnotation(newEventDto.getAnnotation());
        }

        if (!Objects.isNull(newEventDto.getCategory())) {
            currentEvent.setCategory(categoryService.getCategoryById(newEventDto.getCategory()));
        }

        if (!Objects.isNull(newEventDto.getDescription())) {
            currentEvent.setDescription(newEventDto.getDescription());
        }

        if (!Objects.isNull(newEventDto.getEventDate())) {
            currentEvent.setEventDate(newEventDto.getEventDate());
        }

        if (!Objects.isNull(newEventDto.getLocation())) {
            currentEvent.setLocationLat(newEventDto.getLocation().getLat());
            currentEvent.setLocationLon(newEventDto.getLocation().getLon());
        }

        if (!Objects.isNull(newEventDto.getPaid())) {
            currentEvent.setPaid(newEventDto.getPaid());
        }

        if (!Objects.isNull(newEventDto.getParticipantLimit())) {
            currentEvent.setParticipantLimit(newEventDto.getParticipantLimit());
        }

        if (!Objects.isNull(newEventDto.getRequestModeration())) {
            currentEvent.setRequestModeration(newEventDto.getRequestModeration());
        }

        if (!Objects.isNull(newEventDto.getTitle())) {
            currentEvent.setTitle(newEventDto.getTitle());
        }

        if (!Objects.isNull(newEventDto.getStateAction())) {
            switch (newEventDto.getStateAction()) {
                case REJECT_EVENT -> currentEvent.setState(EventState.CANCELED);
                case PUBLISH_EVENT -> currentEvent.setState(EventState.PUBLISHED);
                case SEND_TO_REVIEW -> currentEvent.setRequestModeration(false);
                case CANCEL_REVIEW -> currentEvent.setRequestModeration(true);
            }
        }

        eventRepository.save(currentEvent);

        EventDto eventDto = eventMapper.toDto(currentEvent);

        loadViews(List.of(eventDto), currentEvent.getCreatedOn(), currentEvent.getEventDate());
        loadConfirmedRequests(List.of(eventDto));

        return eventDto;
    }
}

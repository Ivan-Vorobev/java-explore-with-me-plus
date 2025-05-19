package ru.practicum.explorewithme.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.events.enumiration.EventState;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.events.repository.EventRepository;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.users.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.users.mapper.ParticipationRequestMapper;
import ru.practicum.explorewithme.users.model.ParticipationRequest;
import ru.practicum.explorewithme.users.model.RequestStatus;
import ru.practicum.explorewithme.users.model.User;
import ru.practicum.explorewithme.users.repository.RequestRepository;
import ru.practicum.explorewithme.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.explorewithme.exception.NotFoundException.notFoundException;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private static final String USER_NOT_FOUND_EXCEPTION_MESSAGE = "Пользователь с идентификатором {0} не найден!";
    private static final String EVENT_NOT_FOUND_EXCEPTION_MESSAGE = "Событие с идентификатором {0} не найдено!";

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public List<ParticipationRequestDto> findAllRequestsByUserId(long userId) {
        return ParticipationRequestMapper.mapToDTO(requestRepository.findParticipationRequestByRequester_Id(userId));
    }

    @Override
    public ParticipationRequestDto save(long userId, long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(notFoundException(EVENT_NOT_FOUND_EXCEPTION_MESSAGE, eventId));

        checkConstraintsForParticipationRequests(event, eventId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(notFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE, userId));

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .requester(user)
                .event(event)
                .status(event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .created(LocalDateTime.now())
                .requestsCount(0)
                .build();

        requestRepository.save(participationRequest);

        return ParticipationRequestMapper.mapToDTO(participationRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long eventId) {

        ParticipationRequest participationRequest = requestRepository.findParticipationRequestByRequester_IdAndEvent_Id(userId, eventId)
                .stream().findFirst().orElseThrow(notFoundException(EVENT_NOT_FOUND_EXCEPTION_MESSAGE, eventId));

        participationRequest.setStatus(RequestStatus.CANCELED);
        requestRepository.save(participationRequest);

        return ParticipationRequestMapper.mapToDTO(participationRequest);
    }

    private void checkConstraintsForParticipationRequests(Event event, long eventId, long userId) {
        if (!isDuplicateRequest(userId, eventId) || !isUserIsInitiator(event, userId)
                || event.getState().equals(EventState.PUBLISHED) || isParticipantLimitEqualRequestsOnEvent(event)) {
            throw new ConflictException("Conflict on adding request");
        }
    }

    private boolean isDuplicateRequest(long userId, long eventId) {
        System.out.println(requestRepository.countParticipationRequestByRequesterIdAndEvent_Id(userId, eventId));
        return requestRepository.countParticipationRequestByRequesterIdAndEvent_Id(userId, eventId) != 0;
    }

    private boolean isUserIsInitiator(Event event, long userId) {
        return event.getInitiator().getId() == userId;
    }

    private boolean isParticipantLimitEqualRequestsOnEvent(Event event) {
        return event.getParticipantLimit() == requestRepository.countParticipationRequestByEvent_Id(event.getId())
                && event.getParticipantLimit() != 0;
    }
}
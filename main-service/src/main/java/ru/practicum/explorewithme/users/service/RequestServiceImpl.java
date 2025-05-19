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
        User user = userRepository.findById(userId)
                .orElseThrow(notFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE, userId));

        checkConstraintsForParticipationRequests(event, eventId, userId);

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .requester(user)
                .event(event)
                .status(event.getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
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
        if (!(checkDuplicateRequest(userId, eventId) && checkInitiatorId(event, userId)
                && checkMemberOfNotPublishedEvent(event, eventId) && event.getState() != EventState.PUBLISHED)) {
            throw new ConflictException("Conflict on adding request");
        }
    }

    private boolean checkDuplicateRequest(long userId, long eventId) {
        return requestRepository.countParticipationRequestByRequesterIdAndEvent_Id(userId, eventId) != 0;
    }

    private boolean checkInitiatorId(Event event, long userId) {
        return event.getInitiator().getId() == userId;
    }

    private boolean checkMemberOfNotPublishedEvent(Event event, long eventId) {
        return event.getParticipantLimit() == requestRepository.countParticipationRequestByEvent_Id(eventId);
    }
}
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

        User user = userRepository.findById(userId)
                .orElseThrow(notFoundException(USER_NOT_FOUND_EXCEPTION_MESSAGE, userId));

        checkDuplicateRequest(userId, eventId);
        checkUserIsInitiator(event, userId);
        checkParticipantLimitEqualRequestsOnEvent(event);
        checkOnNotPublishedEvent(event);

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .requester(user)
                .event(event)
                .status(event.getRequestModeration() && event.getParticipantLimit() != 0 ? RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .created(LocalDateTime.now())
                .requestsCount(0)
                .build();

        requestRepository.save(participationRequest);

        return ParticipationRequestMapper.mapToDTO(participationRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {

        ParticipationRequest participationRequest = requestRepository.findParticipationRequestByIdAndRequester_Id(requestId, userId)
                .orElseThrow(notFoundException("Запрос {0} на участие пользователя {1}", requestId, userId));
        participationRequest.setStatus(RequestStatus.CANCELED);
        requestRepository.save(participationRequest);

        return ParticipationRequestMapper.mapToDTO(participationRequest);
    }

    private void checkOnNotPublishedEvent(Event event) {
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published");
        }
    }

    private void checkDuplicateRequest(long userId, long eventId) {
        if (requestRepository.countParticipationRequestByRequesterIdAndEvent_Id(userId, eventId) != 0) {
            throw new ConflictException("Duplicate request");
        }
    }

    private void checkUserIsInitiator(Event event, long userId) {
        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("User is Initiator of Request Exception");
        }
    }

    private void checkParticipantLimitEqualRequestsOnEvent(Event event) {
        if (event.getParticipantLimit() == requestRepository.countParticipationRequestByEvent_Id(event.getId())
                && event.getParticipantLimit() != 0) {
            throw new ConflictException("Participant limit is equal to request limit");
        }
    }
}
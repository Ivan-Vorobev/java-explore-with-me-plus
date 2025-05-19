package ru.practicum.explorewithme.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.events.enumiration.EventState;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.events.repository.EventRepository;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
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

        // check povtorniy query
        if (requestRepository.countParticipationRequestByRequester_IdAndEvent_Id(userId, eventId) != 0) {
            throw new ConflictException("duplicate request");
        }

        // check nelzya dobavit request na svoy sobitie
        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("initiator userid");
        }

        // check nelzya ychavstovat' v neopubl sobitii
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("published");
        }

        // check nelzya ychavstovat' v neopubl sobitii
        if (event.getParticipantLimit() == requestRepository.countParticipationRequestByEvent_Id(eventId)) {
            throw new ConflictException("count");
        }
        ParticipationRequest participationRequest = new ParticipationRequest();
        participationRequest.setRequester(user);
        participationRequest.setEvent(event);
        if (!event.getRequestModeration()) {
            participationRequest.setStatus(RequestStatus.CONFIRMED);
        } else {
            participationRequest.setStatus(RequestStatus.PENDING);
        }
        requestRepository.save(participationRequest);

        return ParticipationRequestMapper.mapToDTO(participationRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long eventId) {
        // TODO: change pending to canceled
        // TODO: save
        // TODO: return
        return null;
    }
}

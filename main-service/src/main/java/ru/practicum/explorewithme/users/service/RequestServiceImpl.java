package ru.practicum.explorewithme.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.events.repository.EventRepository;
import ru.practicum.explorewithme.users.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.users.mapper.ParticipationRequestMapper;
import ru.practicum.explorewithme.users.repository.RequestRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> findAllRequestsByUserId(long userId) {
        return ParticipationRequestMapper.mapToDTO(requestRepository.findParticipationRequestByRequester_Id(userId));
    }

    @Override
    public ParticipationRequestDto save(long userId, long eventId) {

        // TODO: check on unique
        // userId of request != eventId -> owner -> 409
        // if eventId -> get Event -> status != PUBLISHED? -> 409
        // check EventId -> Get Event Limit of participants -> 409
        // if event pre-moderation off -> request accepted automatically

        return null;
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long eventId) {
        // TODO: change pending to canceled
        // TODO: save
        // TODO: return
        return null;
    }
}

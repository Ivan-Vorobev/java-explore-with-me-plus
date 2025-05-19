package ru.practicum.explorewithme.users.service;

import ru.practicum.explorewithme.users.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> findAllRequestsByUserId(long userId);

    ParticipationRequestDto save(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long eventId);
}

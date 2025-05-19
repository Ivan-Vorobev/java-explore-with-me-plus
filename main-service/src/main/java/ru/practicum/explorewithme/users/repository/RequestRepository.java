package ru.practicum.explorewithme.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.users.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findParticipationRequestByRequester_Id(long userId);

    long countParticipationRequestByRequester_IdAndEvent_Id(long userId, long eventId);

    long countParticipationRequestByEvent_Id(long eventId);
}

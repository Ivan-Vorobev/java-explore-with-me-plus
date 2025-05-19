package ru.practicum.explorewithme.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.explorewithme.users.model.ParticipationRequest;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findParticipationRequestByRequester_Id(long userId);

    long countParticipationRequestByRequesterIdAndEvent_Id(long userId, long eventId);

    long countParticipationRequestByEvent_Id(long eventId);

    List<ParticipationRequest> findParticipationRequestByRequester_IdAndEvent_Id(Long requesterId, Long eventId);
    @Query("SELECT r.event, count(r.id) as requestsCount FROM ParticipationRequest r WHERE r.status = :status AND r.event.id in :eventIds GROUP BY r.event ORDER BY requestsCount ASC")
    List<ParticipationRequest> findEventsCountByStatus(@Param("eventIds") List<Long> eventIds, @Param("status") RequestStatus status);
}

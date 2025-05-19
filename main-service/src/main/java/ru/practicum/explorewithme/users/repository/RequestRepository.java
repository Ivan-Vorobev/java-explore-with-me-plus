package ru.practicum.explorewithme.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.users.model.ParticipationRequest;
import ru.practicum.explorewithme.users.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findParticipationRequestByRequester_Id(long userId);

    @Query("SELECT r.event, count(r.id) as requestsCount FROM ParticipationRequest r WHERE r.status = :status AND r.event.id in :eventIds GROUP BY r.event ORDER BY requestsCount ASC")
    List<ParticipationRequest> findEventsCountByStatus(@Param("eventIds") List<Long> eventIds, @Param("status") RequestStatus status);
}

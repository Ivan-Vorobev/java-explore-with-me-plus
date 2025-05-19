package ru.practicum.explorewithme.users.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.explorewithme.events.model.Event;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
@Table(name = "participation_requests", schema = "public")
public class ParticipationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    @Column(nullable = false)
    private LocalDateTime created = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;

    @Transient
    private Integer requestsCount;
}
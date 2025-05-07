package ru.practicum.explorewithme.stats.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "queries", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String app;

    @Column(nullable = false)
    String uri;

    @Column(nullable = false, length = 45)
    String ip;

    @Column(nullable = false)
    LocalDateTime timestamp;
}

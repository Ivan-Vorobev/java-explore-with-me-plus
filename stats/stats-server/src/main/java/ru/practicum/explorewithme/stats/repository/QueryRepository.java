package ru.practicum.explorewithme.stats.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface QueryRepository extends JpaRepository<Query, Long> {

    @Query("SELECT q.app, q.uri, COUNT(q) " +
            "FROM Query q " +
            "WHERE q.timestamp BETWEEN :start AND :end " +
            "GROUP BY q.app, q.uri")
    List<Object[]> findAllStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT q.app, q.uri, COUNT(DISTINCT q.ip) " +
            "FROM Query q " +
            "WHERE q.timestamp BETWEEN :start AND :end " +
            "GROUP BY q.app, q.uri")
    List<Object[]> findUniqueIpStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT q.app, q.uri, COUNT(q) " +
            "FROM Query q " +
            "WHERE q.timestamp BETWEEN :start AND :end AND q.uri IN :uris " +
            "GROUP BY q.app, q.uri")
    List<Object[]> findAllStatsForUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);

    @Query("SELECT q.app, q.uri, COUNT(DISTINCT q.ip) " +
            "FROM Query q " +
            "WHERE q.timestamp BETWEEN :start AND :end AND q.uri IN :uris " +
            "GROUP BY q.app, q.uri")
    List<Object[]> findUniqueIpStatsForUris(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("uris") List<String> uris);
}
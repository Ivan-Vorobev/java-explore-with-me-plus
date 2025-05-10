package ru.practicum.explorewithme.stats.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatDTO;
import ru.practicum.explorewithme.stats.mapper.QueryMapper;
import ru.practicum.explorewithme.stats.repository.QueryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueryService {

    private final QueryRepository queryRepository;
    private final QueryMapper queryMapper;

    public List<StatDTO> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<Object[]> results;

        if (uris == null || uris.isEmpty()) {
            if (unique) {
                results = queryRepository.findUniqueIpStats(start, end);
            } else {
                results = queryRepository.findAllStats(start, end);
            }
        } else {
            if (unique) {
                results = queryRepository.findUniqueIpStatsForUris(start, end, uris);
            } else {
                results = queryRepository.findAllStatsForUris(start, end, uris);
            }
        }

        return results.stream()
                .map(this::mapToStatDto)
                .collect(Collectors.toList());
    }

    private StatDTO mapToStatDto(Object[] row) {
        return StatDTO.builder()
                .app((String) row[0])
                .uri((String) row[1])
                .hits(((Number) row[2]).longValue())
                .build();
    }
}
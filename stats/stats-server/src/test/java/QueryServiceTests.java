import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.dto.StatDTO;
import ru.practicum.explorewithme.stats.mapper.QueryMapper;
import ru.practicum.explorewithme.stats.repository.QueryRepository;
import ru.practicum.explorewithme.stats.service.QueryService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QueryServiceTests {

    private QueryRepository queryRepository;
    private QueryMapper queryMapper;
    private QueryService queryService;

    @BeforeEach
    void setUp() {
        queryRepository = mock(QueryRepository.class);
        queryMapper = mock(QueryMapper.class);
        queryService = new QueryService(queryRepository, queryMapper);
    }

    @Test
    void testGetStatsWithUniqueFalseAndNoUris() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<Object[]> repoResult = List.of(
                new Object[]{"ewm-main-service", "/events/1", 5L},
                new Object[]{"ewm-main-service", "/events/2", 10L}
        );

        when(queryRepository.findAllStats(start, end)).thenReturn(repoResult);

        List<StatDTO> result = queryService.getStats(start, end, null, false);

        assertEquals(2, result.size());
        assertEquals("/events/1", result.get(0).getUri());
        assertEquals(5L, result.get(0).getHits());
        assertEquals("/events/2", result.get(1).getUri());
        assertEquals(10L, result.get(1).getHits());
    }

    @Test
    void testGetStatsWithUniqueTrueAndUris() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);
        List<String> uris = List.of("/events/1");

        List<Object[]> repoResult = List.<Object[]>of(
                new Object[]{"ewm-main-service", "/events/1", 3L}
        );

        when(queryRepository.findUniqueIpStatsForUris(start, end, uris)).thenReturn(repoResult);

        List<StatDTO> result = queryService.getStats(start, end, uris, true);

        assertEquals(1, result.size());
        assertEquals("/events/1", result.get(0).getUri());
        assertEquals(3L, result.get(0).getHits());
    }
}
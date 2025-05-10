package ru.practicum.ewm.hits.service;

import org.springframework.stereotype.Service;
import ru.practicum.dto.CreateHitDTO;

@Service("hitService")
public interface HitService {
    void createHit(CreateHitDTO createHitDTO);
}

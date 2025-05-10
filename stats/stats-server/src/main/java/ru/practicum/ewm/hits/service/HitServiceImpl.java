package ru.practicum.ewm.hits.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.HitMapper;
import ru.practicum.dto.CreateHitDTO;
import ru.practicum.ewm.stats.repository.HitsRepository;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
public class HitServiceImpl implements HitService {

    private final HitsRepository hitsRepository;
    private final HitMapper hitMapper;

    @Override
    public void createHit(CreateHitDTO createHitDTO) {
        hitsRepository.save(hitMapper.mapToHit(createHitDTO));
    }
}

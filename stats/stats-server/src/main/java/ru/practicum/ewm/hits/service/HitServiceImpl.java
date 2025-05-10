package ru.practicum.ewm.hits.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.mapper.HitMapper;
import ru.practicum.dto.CreateHitDTO;
import ru.practicum.ewm.stats.repository.HitsRepository;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional(readOnly = true)
public class HitServiceImpl implements HitService {

    private final HitsRepository hitsRepository;
    private final HitMapper hitMapper;

    @Override
    @Transactional
    public void createHit(CreateHitDTO createHitDTO) {
        hitsRepository.save(hitMapper.mapToHit(createHitDTO));
    }
}

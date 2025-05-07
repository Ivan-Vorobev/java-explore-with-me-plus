package ru.practicum.explorewithme.stats.mapper;

import org.mapstruct.Mapper;
import ru.practicum.dto.InputQueryRequest;
import ru.practicum.explorewithme.stats.model.Query;

@Mapper
public interface QueryMapper {
    Query mapToDTO(InputQueryRequest request);
}

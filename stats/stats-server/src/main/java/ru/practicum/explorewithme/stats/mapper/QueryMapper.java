package ru.practicum.explorewithme.stats.mapper;

import ru.practicum.dto.InputQueryRequest;
import ru.practicum.explorewithme.stats.model.Query;

public class QueryMapper {

    public static Query mapToModel(InputQueryRequest inputQueryRequest) {
        return Query.builder()
                .app(inputQueryRequest.getApp())
                .ip(inputQueryRequest.getIp())
                .uri(inputQueryRequest.getUri())
                .timestamp(inputQueryRequest.getTimestamp())
                .build();
    }
}

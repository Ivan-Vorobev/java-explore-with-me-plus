package ru.practicum.explorewithme.users.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.users.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.users.model.ParticipationRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    ParticipationRequest mapToModel(ParticipationRequestDto participationRequestDto);

    ParticipationRequestDto mapToDto(ParticipationRequest participationRequest);

    List<ParticipationRequestDto> mapToDto(List<ParticipationRequest> participationRequests);
}

package ru.practicum.explorewithme.events.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.config.CustomLocalDateTimeDeserializer;
import ru.practicum.explorewithme.config.CustomLocalDateTimeSerializer;
import ru.practicum.explorewithme.constraint.EventStartDateTime;

import java.time.LocalDateTime;

@Data
@Builder
public class NewEventDto {
    @NotBlank
    private String title;
    @NotBlank
    private String annotation;
    @NotBlank
    private String description;
    @NotNull
    private Long category;
    @NotNull
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class) // Для входящих данных
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)     // Для исходящих данных
    @EventStartDateTime
    private LocalDateTime eventDate; // Формат: yyyy-MM-dd HH:mm:ss
    @NotNull
    private LocationDto location;
    @NotNull
    private Integer participantLimit;
    @NotNull
    private Boolean paid;
    @NotNull
    private Boolean requestModeration;
}

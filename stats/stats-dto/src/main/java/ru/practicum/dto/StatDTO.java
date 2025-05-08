package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.validator.ValidEndpoint;

@Data
@Builder
public class StatDTO {

    @NotBlank
    private String app;

    @ValidEndpoint
    private String uri;

    @NotNull
    private Long hits;
}
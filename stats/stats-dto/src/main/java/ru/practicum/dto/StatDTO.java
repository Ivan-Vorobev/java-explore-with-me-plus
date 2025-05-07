package ru.practicum.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatDTO {

    @NotBlank
    String app;

    @Pattern(
            regexp = "^/[a-zA-Z0-9_-]+(/[a-zA-Z0-9_-]+)*/?$",
            message = "Invalid endpoint format (e.g., '/events/1')"
    )
    String uri;

    @NotNull
    Long hits;
}
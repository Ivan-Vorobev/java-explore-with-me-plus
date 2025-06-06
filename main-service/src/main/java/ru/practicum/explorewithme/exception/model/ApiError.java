package ru.practicum.explorewithme.exception.model;

import org.springframework.http.HttpStatus;

public record ApiError(HttpStatus status, String title, String message, String stackTrace) {
}

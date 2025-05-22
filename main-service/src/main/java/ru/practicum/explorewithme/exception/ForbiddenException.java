package ru.practicum.explorewithme.exception;

import java.util.function.Supplier;

public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public static Supplier<ForbiddenException> forbiddenException(String message) {
        return () -> new ForbiddenException(message);
    }
}
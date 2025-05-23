package ru.practicum.explorewithme.exception;

import java.text.MessageFormat;
import java.util.function.Supplier;

public class DataAlreadyExistException extends RuntimeException {

    public DataAlreadyExistException(String message) {
        super(message);
    }

    public DataAlreadyExistException(String message, Object... args) {
        super(MessageFormat.format(message, args));
    }

    public static Supplier<DataAlreadyExistException> dataAlreadyExistException(String message, Object... args) {
        return () -> new DataAlreadyExistException(message, args);
    }

    public static Supplier<DataAlreadyExistException> dataAlreadyExistException(String message) {
        return () -> new DataAlreadyExistException(message);
    }
}
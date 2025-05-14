package ru.practicum.explorewithme.exception;

public class DataAlreadyExistException extends RuntimeException {
    public DataAlreadyExistException() {
    }

    public DataAlreadyExistException(String message) {
        super(message);
    }

    public DataAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAlreadyExistException(Throwable cause) {
        super(cause);
    }
}
package ru.practicum.explorewithme.exception;

public class RelatedDataDeleteException extends RuntimeException {
    public RelatedDataDeleteException() {
    }

    public RelatedDataDeleteException(String message) {
        super(message);
    }

    public RelatedDataDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public RelatedDataDeleteException(Throwable cause) {
        super(cause);
    }
}

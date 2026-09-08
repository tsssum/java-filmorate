package ru.yandex.practicum.filmorate.exception;

public class EmptyStringException extends RuntimeException {
    public EmptyStringException(String message) {
        super(message);
    }
}

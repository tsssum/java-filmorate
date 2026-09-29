package ru.yandex.practicum.filmorate.exception;

public class OverLengthException extends RuntimeException {
    public OverLengthException(String message) {
        super(message);
    }
}

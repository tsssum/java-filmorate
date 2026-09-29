package ru.yandex.practicum.filmorate.exception;

import jakarta.validation.ValidationException;

public class OverLengthException extends ValidationException {
    public OverLengthException(String message) {
        super(message);
    }
}

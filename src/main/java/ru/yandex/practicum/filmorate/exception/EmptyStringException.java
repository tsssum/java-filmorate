package ru.yandex.practicum.filmorate.exception;

import jakarta.validation.ValidationException;

public class EmptyStringException extends ValidationException {
    public EmptyStringException(String message) {
        super(message);
    }
}

package ru.yandex.practicum.filmorate.exception;

import jakarta.validation.ValidationException;

public class NegativeDurationException extends ValidationException {
    public NegativeDurationException(String message) {
        super(message);
    }
}

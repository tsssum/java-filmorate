package ru.yandex.practicum.filmorate.exception;

import jakarta.validation.ValidationException;

public class DateException extends ValidationException {
    public DateException(String message) {
        super(message);
    }
}

package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@lombok.Data
public class Film {
    Long id;
    @NotBlank
    String name;
    @NotBlank
    @Max(200)
    String description;
    @NotNull
    LocalDate releaseDate;
    @Positive
    int duration;
}

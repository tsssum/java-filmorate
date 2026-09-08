package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;
import java.time.LocalDate;

@lombok.Data
public class Film {
    Long id;
    @NotNull @NotBlank
    String name;
    @NotNull @NotBlank
    String description;
    @NotNull
    LocalDate releaseDate;
    Duration duration;
}

package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.Set;

@lombok.Data
@EqualsAndHashCode(exclude = "likes")
public class Film {
    Long id;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    @NotNull
    LocalDate releaseDate;
    GENRE genre;
    MPA mpa;
    @Positive
    int duration;
    Set<Long> likes;
}

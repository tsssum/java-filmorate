package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "likes")
public class Film {
    Long id;
    @NotBlank
    String name;
    @NotNull
    LocalDate releaseDate;
    @Size(max = 200)
    String description;
    @Positive
    int duration;
    MPA mpa;
    Set<GENRE> genres = new LinkedHashSet<>();
    Set<Director> director = new LinkedHashSet<>();
    Set<Long> likes = new HashSet<>();

    @JsonIgnore
    public Film(long filmId, String title, String description, LocalDate releaseDate, Set<Director> directors,
                Set<GENRE> genres, MPA mpa, String duration) {
        this.id = filmId;
        this.name = title;
        this.releaseDate = releaseDate;
        this.description = description;
        this.duration = Integer.parseInt(duration);
        this.mpa = mpa;
        this.genres = genres;
        this.director = directors;
    }

}

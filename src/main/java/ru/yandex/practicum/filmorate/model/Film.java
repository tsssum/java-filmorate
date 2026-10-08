package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@lombok.Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "likes")
public class Film {
    Long id;
    @NotBlank
    String name;
    @Size(max = 200)
    String description;
    @NotNull
    LocalDate releaseDate;
    Set<GENRE> genres;
    MPA mpa;
    @Positive
    int duration;
    Set<Long> likes;

    @JsonIgnore
    public Film(long filmId, String title, String description, LocalDate releaseDate,
                Set genres, Integer mpaId, String duration) {
        this.id = filmId;
        this.name = title;
        this.description = description;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.mpa = (mpaId == null || mpaId == 0) ? null : MPA.fromId(mpaId);
        this.duration = Integer.parseInt(duration);
    }

}

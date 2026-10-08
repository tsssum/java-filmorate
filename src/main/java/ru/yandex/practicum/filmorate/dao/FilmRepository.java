package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GENRE;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface FilmRepository {
    Optional<Film> findById(long id);

    Collection<Film> findAll();

    Optional<Film> create(Film film);

    void addLike(long userId, long filmId);

    void removeLike(long userId, long filmId);

    void delete(Film film);

    Optional<Film> update(Film filmId);

    Set<GENRE> findGenresByFilmId(long id);
}

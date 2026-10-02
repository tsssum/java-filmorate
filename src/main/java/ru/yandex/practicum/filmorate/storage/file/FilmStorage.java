package ru.yandex.practicum.filmorate.storage.file;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Optional<Film> findById(Long id);

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);

    void delete(Film film);
}

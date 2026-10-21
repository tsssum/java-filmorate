package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorRepository {
    Optional<Director> findById(long id);

    Collection<Director> findAll();

    Optional<Director> create(Director director);

    Optional<Director> update(Director director);

    void delete(long directorId);
}

package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.GENRE;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface GenreRepository {
    public Collection<GENRE> getAll();

    public Optional<GENRE> getById(int id);

    Collection<GENRE> getByIds(Collection<Integer> ids);
}
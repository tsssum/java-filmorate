package ru.yandex.practicum.filmorate.dao;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.GENRE;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository {
    public List<GENRE> getAll() {
        return List.of(GENRE.values());
    }

    public Optional<GENRE> getById(int id) {
        return Arrays.stream(GENRE.values())
                .filter(g -> g.getId() == id)
                .findFirst();
    }
}
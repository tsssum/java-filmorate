package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.GENRE;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class GenreService {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Collection<GENRE> getAll() {
        return genreRepository.getAll();
    }

    public Optional<GENRE> getById(int id) {
        if (genreRepository.getById(id).isEmpty()) {
            throw new NotFoundException("Genre with id " + id + " not found");
        } else return genreRepository.getById(id);
    }

    Collection<GENRE> getByIds(Collection<Integer> ids) {
        return genreRepository.getByIds(ids);
    }
}

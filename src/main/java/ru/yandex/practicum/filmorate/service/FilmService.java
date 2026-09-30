package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DateException;
import ru.yandex.practicum.filmorate.exception.EmptyStringException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.file.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    final LocalDate minDate = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        if (filmStorage.findAll().isEmpty()) {
            throw new NotFoundException("No films found");
        }
        return filmStorage.findAll();
    }

    public Optional<Film> create(Film film) {
        if (film.getReleaseDate().isBefore(minDate)) {
            throw new DateException("Дата релиза не может быть раньше " + minDate);
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return filmStorage.create(film);
    }

    public Optional<Film> update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new EmptyStringException("Id должен быть указан");
        }
        if (filmStorage.findById(newFilm.getId()).isPresent()) {
            return filmStorage.update(newFilm);
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    public void delete(Film film) {
        if (filmStorage.findById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        filmStorage.delete(film);
    }

    public Optional<Film> findById(Long id) {
        if (filmStorage.findById(id).isEmpty()) {
            throw new NotFoundException("Film with id " + id + " not found");
        } else return filmStorage.findById(id);
    }

    public void setLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.findById(filmId);
        Optional<User> user = userStorage.findById(userId);
        if (film.isPresent() && user.isPresent()) {
            Set<Long> likes = film.get().getLikes();
            likes.add(user.get().getId());
            film.get().setLikes(likes);
        }
    }

    public void deleteLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.findById(filmId);
        Optional<User> user = userStorage.findById(userId);
        if (film.isPresent() && user.isPresent()) {
            Set<Long> likes = film.get().getLikes();
            likes.remove(user.get().getId());
            film.get().setLikes(likes);
        }
    }

    public List<Film> getPopular(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be greater than 0");
        }
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed()
                        .thenComparing(Film::getId))
                .limit(count)
                .collect(Collectors.toList());
    }
}

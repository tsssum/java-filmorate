package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.DateException;
import ru.yandex.practicum.filmorate.exception.EmptyStringException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GENRE;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreRepository genreRepository;

    final LocalDate minDate = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmDbStorage filmStorage, UserDbStorage userStorage, MpaDbStorage mpaStorage, GenreRepository genreRepository) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaStorage = mpaStorage;
        this.genreRepository = genreRepository;
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Optional<Film> create(Film film) {
        if (film.getReleaseDate().isBefore(minDate)) {
            throw new DateException("Дата релиза не может быть раньше " + minDate);
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        if (film.getMpa() != null && mpaStorage.getById(film.getMpa().getId()).isEmpty()) {
            throw new NotFoundException("Рейтинг не найден");
        }
        if (film.getGenres() != null) {
            for (GENRE g : film.getGenres()) {
                if (genreRepository.getById(g.getId()).isEmpty()) {
                    throw new NotFoundException("Жанр не найден: " + g.getId());
                }
            }
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
        if (film.isEmpty()) throw new NotFoundException("Film with id " + filmId + " not found");
        if (user.isEmpty()) throw new NotFoundException("User with id " + userId + " not found");
    }

    public void deleteLike(Long filmId, Long userId) {
        Optional<Film> film = filmStorage.findById(filmId);
        Optional<User> user = userStorage.findById(userId);
        if (film.isPresent() && user.isPresent()) {
            Set<Long> likes = film.get().getLikes();
            likes.remove(user.get().getId());
            film.get().setLikes(likes);
        }
        if (film.isEmpty()) throw new NotFoundException("Film with id " + filmId + " not found");
        if (user.isEmpty()) throw new NotFoundException("User with id " + userId + " not found");
    }

    public List<Film> getPopular(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("count must be greater than 0");
        }
        return filmStorage.findAll().stream().sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed().thenComparing(Film::getId)).limit(count).collect(Collectors.toList());
    }
}

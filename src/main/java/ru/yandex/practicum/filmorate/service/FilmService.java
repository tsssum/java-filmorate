package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmRepository;
import ru.yandex.practicum.filmorate.dao.GenreRepository;
import ru.yandex.practicum.filmorate.dao.MpaRepository;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.exception.DateException;
import ru.yandex.practicum.filmorate.exception.EmptyStringException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GENRE;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;

    final LocalDate minDate = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(FilmRepository filmRepository, UserRepository userRepository,
                       MpaRepository mpaRepository, GenreRepository genreRepository) {
        this.filmRepository = filmRepository;
        this.userRepository = userRepository;
        this.mpaRepository = mpaRepository;
        this.genreRepository = genreRepository;
    }

    public Collection<Film> findAll() {
        return filmRepository.findAll();
    }

    public Optional<Film> create(Film film) {
        validateFilm(film);

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }

        return filmRepository.create(film);
    }

    public Optional<Film> update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new EmptyStringException("Id должен быть указан");
        }
        if (filmRepository.findById(newFilm.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        validateFilm(newFilm);

        return filmRepository.update(newFilm);
    }

    public void delete(Film film) {
        if (filmRepository.findById(film.getId()).isEmpty()) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        filmRepository.delete(film);
    }

    public Optional<Film> findById(Long id) {
        Optional<Film> film = filmRepository.findById(id);
        if (film.isEmpty()) {
            throw new NotFoundException("Film with id " + id + " not found");
        } else return film;
    }

    public void setLike(Long filmId, Long userId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id " + filmId + " not found"));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        filmRepository.addLike(userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmRepository.findById(filmId)
                .orElseThrow(() -> new NotFoundException("Film with id " + filmId + " not found"));
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));

        filmRepository.removeLike(userId, filmId);
    }

    public List<Film> getPopular(int count) {
        if (count <= 0) throw new IllegalArgumentException("count must be greater than 0");
        return filmRepository.getPopular(count);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(minDate)) {
            throw new DateException("Дата релиза не может быть раньше " + minDate);
        }
        if (film.getMpa() != null && mpaRepository.getById(film.getMpa().getId()).isEmpty()) {
            throw new NotFoundException("Рейтинг не найден");
        }
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Integer> genreIds = film.getGenres().stream()
                    .map(GENRE::getId)
                    .distinct()
                    .toList();

            Collection<GENRE> found = genreRepository.getByIds(genreIds);

            if (found.size() < genreIds.size()) {
                Set<Integer> foundIds = found.stream()
                        .map(GENRE::getId)
                        .collect(Collectors.toSet());
                List<Integer> missing = genreIds.stream()
                        .filter(id -> !foundIds.contains(id))
                        .toList();
                throw new NotFoundException("Жанры не найдены: " + missing);
            }
        }
    }
}

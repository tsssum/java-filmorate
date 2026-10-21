package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);

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
        logger.trace("Creating film {}", film);

        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        logger.trace("Film {} successfully created", film);
        return filmRepository.create(film);
    }

    public Optional<Film> update(Film newFilm) {
        if (newFilm.getId() == null) {
            logger.error("New film id is null");
            throw new EmptyStringException("Id должен быть указан");
        }
        if (filmRepository.findById(newFilm.getId()).isEmpty()) {
            logger.error("Film with id {} not found", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }

        validateFilm(newFilm);
        logger.trace("Updating film {}", newFilm);
        return filmRepository.update(newFilm);
    }

    public void delete(Long filmId) {
        if (filmRepository.findById(filmId).isEmpty()) {
            logger.error("Film with id {} not found", filmId);
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        logger.trace("Deleting film {}", filmId);
        filmRepository.delete(filmId);
    }

    public Optional<Film> findById(Long id) {
        Optional<Film> film = filmRepository.findById(id);
        logger.trace("Searching for film with id {}", id);
        if (film.isEmpty()) {
            logger.error("Film with id {} not found", id);
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

    public Collection<Film> getPopular(int count, Long genreId, Long year) {
        if (count <= 0) throw new IllegalArgumentException("count must be greater than 0");
        return filmRepository.getPopular(count, genreId, year);
    }

    public Collection<Film> getCommon(Long userId, Long friendId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("User with id " + friendId + " not found"));
        return filmRepository.getCommon(userId, friendId);
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

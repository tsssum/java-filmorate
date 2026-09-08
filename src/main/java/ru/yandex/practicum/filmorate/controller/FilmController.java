package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<Long, Film>();
    final Integer MAX_DESCRIPTION_LENGTH = 200;
    final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName().isBlank()) {
            throw new EmptyStringException("Название не может быть пустым");
        }
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            throw new OverLengthException("Название не может быть больше " + MAX_DESCRIPTION_LENGTH + " символов");
        }

        if (film.getReleaseDate().isBefore(MIN_DATE)) {
            throw new DateException("Дата релиза не может быть раньше " + MIN_DATE);
        }

        if (film.getDuration().isNegative()) {
            throw new NegativeDurationException("Продолжительность фильма не может быть отрицательной");
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        if (newFilm.getId() == null) {
            throw new EmptyStringException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (!newFilm.getName().isBlank()) {
                oldFilm.setName(newFilm.getName());
            }
            if (!newFilm.getDescription().isBlank()) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

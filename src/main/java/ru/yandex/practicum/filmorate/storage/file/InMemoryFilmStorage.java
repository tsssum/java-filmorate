package ru.yandex.practicum.filmorate.storage.file;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DateException;
import ru.yandex.practicum.filmorate.exception.EmptyStringException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<Long, Film>();
    final Integer maxDescriptionLength = 200;
    final LocalDate minDate = LocalDate.of(1895, 12, 28);

    @Override
    public Collection<Film> findAll() {
        if (films.isEmpty()) {
            throw new NotFoundException("No films found");
        }
        return films.values();
    }

    @Override
    public Film findById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new NotFoundException("Film not found");
        }
    }

    @Override
    public Film create(Film film) {
        if (film.getReleaseDate().isBefore(minDate)) {
            throw new DateException("Дата релиза не может быть раньше " + minDate);
        }
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (newFilm.getId() == null) {
            throw new EmptyStringException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() != null && !newFilm.getName().isBlank()) {
                oldFilm.setName(newFilm.getName());
            }
            if (newFilm.getDescription() != null && !newFilm.getDescription().isBlank()
                    && newFilm.getDescription().length() <= maxDescriptionLength) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (newFilm.getReleaseDate() != null && newFilm.getReleaseDate().isAfter(minDate)) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (newFilm.getDuration() > 0) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public void delete(Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
        }
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

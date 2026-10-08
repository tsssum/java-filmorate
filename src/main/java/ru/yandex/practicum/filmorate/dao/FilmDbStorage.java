package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GENRE;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> new Film(
            rs.getLong("FILM_ID"),
            rs.getString("TITLE"),
            rs.getString("DESCRIPTION"),
            rs.getDate("RELEASE_DATE").toLocalDate(),
            findGenresByFilmId(rs.getLong("FILM_ID")),
            rs.getObject("MPA_id", Integer.class),
            rs.getString("DURATION")
    );

    @Override
    public Optional<Film> findById(long id) {
        String sql = "select * from FILMS where FILM_ID = :id";
        return jdbcTemplate.query(sql, Map.of("id", id), filmRowMapper).stream().findFirst();
    }

    @Override
    public Collection<Film> findAll() {
        String sql = "SELECT * FROM FILMS";
        return jdbcTemplate.query(sql, filmRowMapper);
    }

    @Override
    public Optional<Film> create(Film film) {
        String sql = "INSERT INTO FILMS (TITLE, DESCRIPTION, RELEASE_DATE, MPA_id, DURATION) " +
                "VALUES (:title, :description, :release_date, :mpa_id, :duration)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql,
                new MapSqlParameterSource()
                        .addValue("title", film.getName())
                        .addValue("description", film.getDescription())
                        .addValue("release_date", film.getReleaseDate())
                        .addValue("mpa_id", film.getMpa() != null ? film.getMpa().getId() : null)
                        .addValue("duration", film.getDuration()),
                keyHolder);
        film.setId(keyHolder.getKey().longValue());
        saveGenres(film);
        return Optional.of(film);
    }

    @Override
    public void addLike(long userId, long filmId) {
        String sql = "INSERT INTO USERS_FILMS_LIKES (User_id, Film_id) " +
                "VALUES (:user_id, :film_id)";
        jdbcTemplate.update(sql, Map.of(
                "user_id", userId,
                "film_id", filmId
        ));
    }

    @Override
    public void removeLike(long userId, long filmId) {
        String sql = "DELETE FROM USERS_FILMS_LIKES WHERE User_id = :user_id AND Film_id = :film_id";
        jdbcTemplate.update(sql, Map.of("user_id", userId, "film_id", filmId));
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = "SELECT f.* FROM FILMS f " +
                "LEFT JOIN USERS_FILMS_LIKES l ON l.Film_id = f.FILM_ID " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(l.User_id) DESC, f.FILM_ID ASC " +
                "LIMIT :count";
        return jdbcTemplate.query(sql, Map.of("count", count), filmRowMapper);
    }

    @Override
    public void delete(Film film) {
        String sql = "DELETE FROM FILMS WHERE FILM_ID = :id";
        jdbcTemplate.update(sql, Map.of("id", film.getId()));
    }

    @Override
    public Optional<Film> update(Film film) {
        String sql = "UPDATE FILMS SET TITLE = :title, DESCRIPTION = :description, " +
                "RELEASE_DATE = :release_date, MPA_id = :mpa_id, DURATION = :duration " +
                "WHERE FILM_ID = :id";
        jdbcTemplate.update(sql, new MapSqlParameterSource()
                .addValue("id", film.getId())
                .addValue("title", film.getName())
                .addValue("description", film.getDescription())
                .addValue("release_date", film.getReleaseDate())
                .addValue("mpa_id", film.getMpa() != null ? film.getMpa().getId() : null)
                .addValue("duration", film.getDuration()));
        saveGenres(film);
        return Optional.of(film);
    }

    @Override
    public HashSet<GENRE> findGenresByFilmId(long id) {
        String sql = "SELECT GENRE_ID FROM FILM_GENRES WHERE FILM_ID = :id ORDER BY GENRE_ID";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, Map.of("id", id),
                (rs, rowNum) -> GENRE.fromId(rs.getInt("GENRE_ID"))));
    }

    private void saveGenres(Film film) {
        jdbcTemplate.update("DELETE FROM FILM_GENRES WHERE FILM_ID = :id",
                Map.of("id", film.getId()));

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (:film_id, :genre_id)";
        for (GENRE g : new HashSet<>(film.getGenres())) {
            jdbcTemplate.update(sql, Map.of(
                    "film_id", film.getId(),
                    "genre_id", g.getId()
            ));
        }
    }
}

package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.GENRE;
import ru.yandex.practicum.filmorate.model.MPA;

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
            new HashSet(),
            rs.getObject("MPA_id", Integer.class) == null
                    ? null
                    : MPA.fromId(rs.getInt("MPA_id")),
            rs.getString("DURATION")
    );

    @Override
    public Optional<Film> findById(long id) {
        String sql = "SELECT FILM_ID, TITLE, DESCRIPTION, RELEASE_DATE, DURATION, MPA_id " +
                "FROM FILMS WHERE FILM_ID = :id";
        return jdbcTemplate.query(sql, Map.of("id", id), filmRowMapper)
                .stream()
                .findFirst()
                .map(f -> {
                    f.setGenres(new HashSet<>(findGenresByFilmId(f.getId())));
                    return f;
                });
    }

    @Override
    public Collection<Film> findAll() {
        String sql = """
                SELECT f.FILM_ID, f.TITLE, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION,
                       m.MPA_id
                FROM FILMS f
                LEFT JOIN MPA m ON f.MPA_id = m.MPA_id
                ORDER BY f.FILM_ID
                """;

        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        Map<Long, Set<GENRE>> genresByFilm = findGenresByFilmIds(filmIds);
        films.forEach(film ->
                film.setGenres(new HashSet<>(
                        genresByFilm.getOrDefault(film.getId(), Set.of())
                ))
        );
        return films;
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
        String sql = """
                SELECT f.*
                FROM FILMS f
                LEFT JOIN USERS_FILMS_LIKES l ON l.Film_id = f.FILM_ID
                GROUP BY f.FILM_ID
                ORDER BY COUNT(l.User_id) DESC, f.FILM_ID ASC
                LIMIT :count
                """;

        List<Film> films = jdbcTemplate.query(sql, Map.of("count", count), filmRowMapper);
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();

        Map<Long, Set<GENRE>> genresByFilm = findGenresByFilmIds(filmIds);
        films.forEach(film ->
                film.setGenres(new HashSet<>(
                        genresByFilm.getOrDefault(film.getId(), Set.of())
                ))
        );
        return films;
    }

    private Map<Long, Set<GENRE>> findGenresByFilmIds(Collection<Long> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {
            return Map.of();
        }
        String sql = """
                SELECT fg.FILM_ID, g.Genre_id
                FROM FILM_GENRES fg
                JOIN GENRE g ON g.Genre_id = fg.Genre_id
                WHERE fg.FILM_ID IN (:filmIds)
                """;

        return jdbcTemplate.query(
                sql,
                Map.of("filmIds", filmIds),
                rs -> {
                    Map<Long, Set<GENRE>> result = new HashMap<>();
                    while (rs.next()) {
                        Long filmId = rs.getLong("FILM_ID");
                        GENRE genre = GENRE.fromId(rs.getInt("Genre_id"));
                        result.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
                    }
                    return result;
                }
        );
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
    public Set<GENRE> findGenresByFilmId(Long filmId) {
        String sql = """
                SELECT g.Genre_id
                FROM FILM_GENRES fg
                JOIN GENRE g ON g.Genre_id = fg.Genre_id
                WHERE fg.FILM_ID = :filmId
                """;

        return new HashSet<>(jdbcTemplate.query(
                sql,
                Map.of("filmId", filmId),
                (rs, rowNum) -> GENRE.fromId(rs.getInt("Genre_id"))
        ));
    }

    private void saveGenres(Film film) {
        jdbcTemplate.update(
                "DELETE FROM FILM_GENRES WHERE FILM_ID = :id",
                Map.of("id", film.getId())
        );

        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return;
        }

        String sql = "INSERT INTO FILM_GENRES (FILM_ID, GENRE_ID) VALUES (:film_id, :genre_id)";

        SqlParameterSource[] batch = new HashSet<>(film.getGenres()).stream()
                .map(g -> new MapSqlParameterSource()
                        .addValue("film_id", film.getId())
                        .addValue("genre_id", g.getId()))
                .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, batch);
    }
}

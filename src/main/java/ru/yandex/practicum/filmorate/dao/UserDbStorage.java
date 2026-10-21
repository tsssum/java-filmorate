package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final FilmRepository filmRepository;

    private static final RowMapper<User> userRowMapper = (rs, rowNum) -> new User(
            rs.getLong("USER_ID"),
            rs.getString("EMAIL"),
            rs.getString("LOGIN"),
            rs.getString("USER_NAME"),
            rs.getDate("BIRTHDAY").toLocalDate()
    );

    @Override
    public Optional<User> findById(long id) {
        String sql = "select * from USERS where USER_ID = :id";
        return jdbcTemplate.query(sql, Map.of("id", id), userRowMapper).stream().findFirst();
    }

    @Override
    public Collection<User> findAll() {
        String sql = "select * from USERS";
        return jdbcTemplate.query(sql, userRowMapper);
    }

    @Override
    public Optional<User> create(User user) {
        String sql = "INSERT INTO USERS (EMAIL, LOGIN, USER_NAME, BIRTHDAY) " +
                "VALUES (:email, :login, :name, :birthday)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql,
                new MapSqlParameterSource()
                        .addValue("email", user.getEmail())
                        .addValue("login", user.getLogin())
                        .addValue("name", user.getName())
                        .addValue("birthday", user.getBirthday()),
                keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return Optional.of(user);
    }

    @Override
    public void addFriend(long userId, long friendId, long statusId) {
        jdbcTemplate.update("INSERT INTO FRIENDSHIP(USER_ID, FRIEND_ID, STATUS_ID) " +
                        "VALUES (:user_id, :friend_id, :status_id)",
                Map.of(
                        "user_id", userId,
                        "friend_id", friendId,
                        "status_id", statusId
                )
        );
    }

    @Override
    public Collection<User> getFriends(long userId) {
        String sql = "SELECT u.* FROM USERS u " +
                "JOIN FRIENDSHIP f ON f.FRIEND_ID = u.USER_ID " +
                "WHERE f.USER_ID = :id";
        return jdbcTemplate.query(sql, Map.of("id", userId), userRowMapper);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long friendId) {
        String sql = """
                SELECT u.USER_ID, u.EMAIL, u.LOGIN, u.USER_NAME, u.BIRTHDAY
                FROM USERS u
                JOIN FRIENDSHIP f1 ON f1.FRIEND_ID = u.USER_ID AND f1.USER_ID = :userId
                JOIN FRIENDSHIP f2 ON f2.FRIEND_ID = u.USER_ID AND f2.USER_ID = :friendId
                ORDER BY u.USER_ID
                """;

        return jdbcTemplate.query(
                sql,
                Map.of("userId", userId, "friendId", friendId),
                userRowMapper
        );
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM FRIENDSHIP " +
                "WHERE USER_ID = :user_id AND FRIEND_ID = :friend_id";
        jdbcTemplate.update(sql, Map.of(
                "user_id", userId,
                "friend_id", friendId
        ));
    }

    @Override
    public void delete(Long userId) {
        String sql = "DELETE FROM USERS WHERE USER_ID = :id";
        jdbcTemplate.update(sql, Map.of("id", userId));
    }

    @Override
    public Optional<User> update(User user) {
        String sql = "UPDATE USERS SET EMAIL = :email, LOGIN = :login, " +
                "USER_NAME = :name, BIRTHDAY = :birthday WHERE USER_ID = :id";
        jdbcTemplate.update(sql, Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        ));
        return Optional.of(user);
    }

    @Override
    public Collection<Film> getRecommendations(long userId) {
        String sql = """
                SELECT DISTINCT f.FILM_ID, f.TITLE, f.DESCRIPTION,
                                f.RELEASE_DATE, f.DURATION, f.MPA_id
                FROM FILMS f
                JOIN USERS_FILMS_LIKES l ON l.Film_id = f.FILM_ID
                WHERE l.User_id IN (
                    SELECT l2.User_id
                    FROM USERS_FILMS_LIKES l1
                    JOIN USERS_FILMS_LIKES l2 ON l2.Film_id = l1.Film_id
                    WHERE l1.User_id = :userId AND l2.User_id <> :userId
                    GROUP BY l2.User_id
                    HAVING COUNT(*) = (
                        SELECT MAX(cnt) FROM (
                            SELECT COUNT(*) AS cnt
                            FROM USERS_FILMS_LIKES a
                            JOIN USERS_FILMS_LIKES b ON b.Film_id = a.Film_id
                            WHERE a.User_id = :userId AND b.User_id <> :userId
                            GROUP BY b.User_id
                        ) sub
                    )
                )
                AND NOT EXISTS (
                    SELECT 1 FROM USERS_FILMS_LIKES mine
                    WHERE mine.Film_id = f.FILM_ID AND mine.User_id = :userId
                )
                ORDER BY f.FILM_ID
                """;
        Collection<Film> films = jdbcTemplate.query(
                sql,
                Map.of("userId", userId),
                (rs, rowNum) -> new Film(
                        rs.getLong("FILM_ID"),
                        rs.getString("TITLE"),
                        rs.getString("DESCRIPTION"),
                        rs.getDate("RELEASE_DATE").toLocalDate(),
                        new LinkedHashSet<>(),
                        new LinkedHashSet<>(),
                        rs.getObject("MPA_id", Integer.class) == null
                                ? null : MPA.fromId(rs.getInt("MPA_id")),
                        rs.getString("DURATION")
                )
        );

        films.forEach(film ->
                film.setGenres(filmRepository.findGenresByFilmId(film.getId()))
        );
        films.forEach(film ->
                film.setDirectors(filmRepository.findDirectorsByFilmId(film.getId()))
        );

        return films;
    }
}

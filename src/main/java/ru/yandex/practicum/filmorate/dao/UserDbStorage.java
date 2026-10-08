package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

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
    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE FROM FRIENDSHIP " +
                "WHERE USER_ID = :user_id AND FRIEND_ID = :friend_id";
        jdbcTemplate.update(sql, Map.of(
                "user_id", userId,
                "friend_id", friendId
        ));
    }

    @Override
    public void delete(User user) {
        String sql = "DELETE FROM USERS WHERE USER_ID = :id";
        jdbcTemplate.update(sql, Map.of("id", user.getId()));
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
}

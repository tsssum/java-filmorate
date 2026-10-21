package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<Director> directorRowMapper = (rs, rowNum) -> new Director(
            rs.getLong("DIRECTOR_ID"),
            rs.getString("DIRECTOR_NAME")
    );

    @Override
    public Optional<Director> findById(long id) {
        String sql = "SELECT DIRECTOR_ID, DIRECTOR_NAME " +
                "FROM DIRECTORS " +
                "WHERE DIRECTOR_ID = :id";
        return jdbcTemplate.query(sql, Map.of("id", id), directorRowMapper)
                .stream()
                .findFirst();
    }

    @Override
    public Collection<Director> findAll() {
        String sql = "SELECT DIRECTOR_ID, DIRECTOR_NAME " +
                "FROM DIRECTORS f " +
                "ORDER BY 1";

        Collection<Director> directors = jdbcTemplate.query(sql, directorRowMapper);
        return directors;
    }

    @Override
    public Optional<Director> create(Director director) {
        String sql = "INSERT INTO DIRECTORS (DIRECTOR_NAME) " +
                "VALUES (:directorName)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(sql,
                new MapSqlParameterSource()
                        .addValue("directorName", director.getName()),
                keyHolder);
        director.setId(keyHolder.getKey().longValue());
        return Optional.of(director);
    }

    @Override
    public Optional<Director> update(Director director) {
        String sql = "UPDATE DIRECTORS SET DIRECTOR_NAME = :directorName " +
                "WHERE DIRECTOR_ID = :id";
        jdbcTemplate.update(sql, Map.of(
                "id", director.getId(),
                "directorName", director.getName())
        );
        return Optional.of(director);
    }

    @Override
    public void delete(long directorId) {
        String sql = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = :directorId";
        jdbcTemplate.update(sql, Map.of("directorId", directorId));
    }
}

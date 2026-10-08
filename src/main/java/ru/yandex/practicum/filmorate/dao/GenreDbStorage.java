package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.GENRE;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final RowMapper<GENRE> genreRowMapper = (rs, rowNum) ->
            GENRE.fromId(rs.getInt("Genre_id"));

    @Override
    public Collection<GENRE> getAll() {
        return jdbcTemplate.query("SELECT * FROM Genre ORDER BY Genre_id", genreRowMapper);
    }

    @Override
    public Optional<GENRE> getById(int id) {
        String sql = "SELECT * FROM Genre WHERE Genre_id = :id";
        return jdbcTemplate.query(sql, Map.of("id", id), genreRowMapper)
                .stream().findFirst();
    }

    @Override
    public Collection<GENRE> getByIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        String sql = "SELECT * FROM Genre WHERE Genre_id IN (:ids) ORDER BY Genre_id";
        return jdbcTemplate.query(sql, Map.of("ids", ids), genreRowMapper);
    }
}
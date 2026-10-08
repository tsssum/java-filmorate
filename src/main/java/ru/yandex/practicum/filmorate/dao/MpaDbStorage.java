package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static final RowMapper<MPA> MPA_ROW_MAPPER = (rs, rowNum) ->
            MPA.fromId(rs.getInt("MPA_id"));

    @Override
    public Optional<MPA> getById(int id) {
        String sql = "SELECT * FROM MPA WHERE MPA_id = :id";
        return jdbcTemplate.query(sql, Map.of("id", id), MPA_ROW_MAPPER)
                .stream().findFirst();
    }

    @Override
    public List<MPA> getAll() {
        return jdbcTemplate.query("SELECT * FROM MPA ORDER BY MPA_id", MPA_ROW_MAPPER);
    }
}
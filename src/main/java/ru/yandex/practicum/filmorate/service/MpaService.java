package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Validation.Exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

@Service
@Slf4j
public class MpaService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<MpaRating> getAllRatings() {
        String sql = "SELECT rating_id as id, rating_name as name FROM rating_mpa ORDER BY rating_id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new MpaRating(rs.getInt("id"), rs.getString("name")));
    }

    public MpaRating getRatingById(int id) {
        String sql = "SELECT rating_id as id, rating_name as name FROM rating_mpa WHERE rating_id = ?";
        List<MpaRating> ratings = jdbcTemplate.query(sql, (rs, rowNum) ->
                new MpaRating(rs.getInt("id"), rs.getString("name")), id);

        if (ratings.isEmpty()) {
            throw new NotFoundException("Рейтинг с ID " + id + " не найден");
        }

        return ratings.get(0);
    }
}
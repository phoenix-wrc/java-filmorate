package ru.yandex.practicum.filmorate.storage.film.Impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmMpaRatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class FilmBDMpaRatingStorage implements FilmMpaRatingStorage {
    @NonNull
    private final JdbcTemplate jdbcTemplate;

    public FilmBDMpaRatingStorage(@NonNull JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Optional<MpaRating>> ratings() {
        //Тут тоже всё просто
        String sql = "SELECT RATING_ID, RATING FROM FILMORATE_MPA_RATING " +
                " ORDER BY RATING_ID ";
        List<Optional<MpaRating>> ratings = jdbcTemplate.query(sql, this::getRating);
        //Пишут что метод квери не может возвращать нулы так что проверки не требуется
        log.debug("Забрали все рэйтинги");
        return ratings;
    }

    @Override
    public Optional<MpaRating> rating(Integer id) {
        String sql = "SELECT * FROM FILMORATE_MPA_RATING " +
                "WHERE RATING_ID = ?";
        return jdbcTemplate.queryForObject(sql, this::getRating, id);
    }

    private Optional<MpaRating> getRating(ResultSet rs, int rowNum) throws SQLException {
        return Optional.of(new MpaRating(
                rs.getInt("RATING_ID"), rs.getString("RATING")));
    }
}
